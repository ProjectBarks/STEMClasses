#!/bin/sh

#Global Variables
EXPORT=./export/
TIMESTAMP_SERVER='http://timestamp.digicert.com'
UPDATER_FILE='Updater.jar'
CORE_FILE='STEMClasses.jar'
NOTIFICATION_FILE='libOSXNotification.dylib'


#Check if a file exists then copy it to the export directory
checkcopy ()
{
    if [ -z $1 ] || [ -z $2 ]; then
       echo 'Empty variable!'
       exit 1
    fi
    if [ ! -f $1 ]; then
        printf '%s %s\n' $2 'not found!'
        exit 1
    fi
    printf '     %s %s\n' 'Copying' $2
    cp $1 ${EXPORT}$2
}

#Signing a jar and all of its classes
sign ()
{
    if [ -z $1 ] || [ -z $2 ]; then
       echo 'Empty variable!'
       exit 1
    fi
    printf '    %s %s\n' 'Signing...' $1
    jarsigner -tsa http://timestamp.digicert.com ${EXPORT}$1 mykey -storepass $2 | grep 'error'
}

usage() {
    echo 'usage: distribute [[-c] [-h]] [-l] [-p (password)]'
    echo '    [-c | --compile]        - Enables compiling of maven projects'
    echo '    [-l | --launch]         - Launch the compiled project after compile'
    echo '    [-h | --help]           - Information about the paramaters'
    echo '    [-p | --password] pas   - The password auto provided to avoid input'
}

#Argument reader
launch=false
while [ "$1" != "" ]; do
    case $1 in
        #Compile - compile all projects in the parent
        -c | --compile )        echo 'Compiling projects...'
                                mvn -q install compile package
                                ;;
        #Launches after compile
        -l | --launch )         launch=true
                                ;;
        #Usage help
        -h | --help )           usage
                                exit
                                ;;
        #Password
        -p | --password)        shift
                                password=$1
                                ;;
        #Invalid param
        * )                     usage
                                exit 1
    esac
    shift
done

echo '=== STEMClasses Distributer ==='
#Cleanup - Delete and recreate directory
echo 'Starting cleanup'
if [ -d ${EXPORT} ]; then
    printf '    %s %s\n' 'Deleteting' ${EXPORT}
    rm -rf ${EXPORT}
fi
 printf '    %s %s\n' 'Creating' ${EXPORT}
mkdir -p ${EXPORT}

#Copy the compiled projects
echo 'Starting copy process'
checkcopy ./Core/target/core-jar-with-dependencies.jar ${CORE_FILE}
checkcopy ./Deployer/target/Deployer-jar-with-dependencies.jar ${UPDATER_FILE}
checkcopy ./resources/libOSXNotification.dylib ${NOTIFICATION_FILE}


#Jar Signer
#Read password
if [ -z ${password} ]; then
    echo -n Keystore Password:
    read -s password
fi
printf '\nStaring signing process\n'
#Sign every jar
sign  ${CORE_FILE} ${password}
sign ${UPDATER_FILE} ${password}

echo 'Starting packager process'
pck_os=${EXPORT}package/Contents/MacOS/
java -jar packr.jar ./resources/package.json | sed 's/^/    /'

echo 'Starting package patcher process'
mv ${pck_os}jre/ ${pck_os}jre_temp/
echo '    Unzipping...'
unzip ./resources/jre.zip -d ${pck_os} | grep 'error'
echo '    Patching...'
rm ${pck_os}jre/lib/rt.jar
cp ${pck_os}jre_temp/lib/rt.jar ${pck_os}jre/lib/rt.jar
cp ./resources/icon.icns ${EXPORT}package/Contents/Resources/icons.icns
rm ${pck_os}STEMClasses
cp ./resources/launcher.sh ${pck_os}STEMClasses
chmod +x ${pck_os}STEMClasses
sed -i -e 's/com\.yourcompany\.identifier/net.\projectbarks.\stemclasses/g' ${EXPORT}package/Contents/Info.plist
echo '    Cleanup...'
rm -rf ${pck_os}jre_temp/
rm -rf ${EXPORT}package/Contents/MacOS/jre/man
rm ${EXPORT}package/Contents/MacOS/jre/lib/libjfxwebkit.dylib
rm ${EXPORT}package/Contents/MacOS/jre/lib/jfxrt.jar
rm -rf ${EXPORT}package/Contents/Info.plist-e
echo '    Patch complete'
mv ${EXPORT}package/ ${EXPORT}STEMClasses.app/
echo 'Package created'

#Launch jar if enabled
if [ ${launch} = true ]; then
    echo 'Launching Application...'
    java -jar ${EXPORT}'Updater.jar'
    echo 'Completed!'
fi