package net.projectbarks.stemclasses.letterday;

/**
 * Created by brandon on 12/5/14.
 */
public enum LetterDayType {


    FIVE_THROUGH_SEVEN("5-7", "c", "g", "k"),
    ONE_THROUGH_FOUR("1-4", "b", "f", "j"),
    ALL("1-7", "a", "d", "e", "h", "i", "l");

    private String representation;
    private String[] days;

    private LetterDayType(String representation, String... days) {
        this.days = days;
        this.representation = representation;
    }

    public static LetterDayType match(String day) {
        day = day.toLowerCase().trim();
        if (containsDay(day, ALL.days)) {
            return ALL;
        } else if (containsDay(day, ONE_THROUGH_FOUR.days)) {
            return ONE_THROUGH_FOUR;
        } else if (containsDay(day, FIVE_THROUGH_SEVEN.days)) {
            return FIVE_THROUGH_SEVEN;
        } else {
            return null;
        }
    }

    private static boolean containsDay(String test, String... days) {
        test = String.valueOf(test.toLowerCase().trim().charAt(0));
        for (String day : days) {
            if (!test.contains(day)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public String getRepresentation() {
        return representation;
    }
}