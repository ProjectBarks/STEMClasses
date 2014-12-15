package net.projectbarks.stemclasses.letterday;

/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Written By: brandon on 12/5/14
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