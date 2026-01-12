package controller.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import controller.utility.CommandParserUtils;
import java.util.Set;
import model.calendar.Weekday;
import org.junit.Test;

/**
 * Unit tests for CommandParserUtils.
 */
public class CommandParserUtilsTest {

  @Test
  public void testParseSingleWeekday() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("M");
    assertEquals(1, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
  }

  @Test
  public void testParseMonday() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("M");
    assertTrue(result.contains(Weekday.MONDAY));
  }

  @Test
  public void testParseTuesday() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("T");
    assertTrue(result.contains(Weekday.TUESDAY));
  }

  @Test
  public void testParseWednesday() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("W");
    assertTrue(result.contains(Weekday.WEDNESDAY));
  }

  @Test
  public void testParseThursday() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("R");
    assertTrue(result.contains(Weekday.THURSDAY));
  }

  @Test
  public void testParseFriday() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("F");
    assertTrue(result.contains(Weekday.FRIDAY));
  }

  @Test
  public void testParseSaturday() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("S");
    assertTrue(result.contains(Weekday.SATURDAY));
  }

  @Test
  public void testParseSunday() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("U");
    assertTrue(result.contains(Weekday.SUNDAY));
  }

  @Test
  public void testParse() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MWF");
    assertEquals(3, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
    assertTrue(result.contains(Weekday.WEDNESDAY));
    assertTrue(result.contains(Weekday.FRIDAY));
  }

  @Test
  public void testParse2() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("TR");
    assertEquals(2, result.size());
    assertTrue(result.contains(Weekday.TUESDAY));
    assertTrue(result.contains(Weekday.THURSDAY));
  }

  @Test
  public void testParseAllWeekdays() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MTWRFSU");
    assertEquals(7, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
    assertTrue(result.contains(Weekday.TUESDAY));
    assertTrue(result.contains(Weekday.WEDNESDAY));
    assertTrue(result.contains(Weekday.THURSDAY));
    assertTrue(result.contains(Weekday.FRIDAY));
    assertTrue(result.contains(Weekday.SATURDAY));
    assertTrue(result.contains(Weekday.SUNDAY));
  }

  @Test
  public void testParseLowerCase() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("mwf");
    assertEquals(3, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
    assertTrue(result.contains(Weekday.WEDNESDAY));
    assertTrue(result.contains(Weekday.FRIDAY));
  }

  @Test
  public void testParseMixedCase() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MwF");
    assertEquals(3, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
    assertTrue(result.contains(Weekday.WEDNESDAY));
    assertTrue(result.contains(Weekday.FRIDAY));
  }

  @Test
  public void testParseUpperCase() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MWF");
    assertEquals(3, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
    assertTrue(result.contains(Weekday.WEDNESDAY));
    assertTrue(result.contains(Weekday.FRIDAY));
  }

  @Test
  public void testParseDuplicateWeekdays() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MMM");
    assertEquals(1, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
  }

  @Test
  public void testParseDuplicatesInMixedString() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MWFMWF");
    assertEquals(3, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
    assertTrue(result.contains(Weekday.WEDNESDAY));
    assertTrue(result.contains(Weekday.FRIDAY));
  }

  @Test
  public void testParseWeekendDays() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("SU");
    assertEquals(2, result.size());
    assertTrue(result.contains(Weekday.SATURDAY));
    assertTrue(result.contains(Weekday.SUNDAY));
  }

  @Test
  public void testParseWeekdays() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MTWRF");
    assertEquals(5, result.size());
    assertTrue(result.contains(Weekday.MONDAY));
    assertTrue(result.contains(Weekday.TUESDAY));
    assertTrue(result.contains(Weekday.WEDNESDAY));
    assertTrue(result.contains(Weekday.THURSDAY));
    assertTrue(result.contains(Weekday.FRIDAY));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidCharacter() {
    CommandParserUtils.parseWeekdays("X");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidCharacterInMiddle() {
    CommandParserUtils.parseWeekdays("MXF");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWithNumber() {
    CommandParserUtils.parseWeekdays("M1F");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWithSpecialCharacter() {
    CommandParserUtils.parseWeekdays("M@F");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEmptyString() {
    CommandParserUtils.parseWeekdays("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWithSpace() {
    CommandParserUtils.parseWeekdays("M F");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWithTab() {
    CommandParserUtils.parseWeekdays("M\tF");
  }

  @Test
  public void testParseInvalidCharacterExceptionMessage() {
    try {
      CommandParserUtils.parseWeekdays("Q");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Invalid weekday character"));
      assertTrue(e.getMessage().contains("Q"));
    }
  }

  @Test
  public void testParseEmptyStringExceptionMessage() {
    try {
      CommandParserUtils.parseWeekdays("");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("At least one weekday must be specified"));
    }
  }

  @Test
  public void testParseAllDaysInOrder() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MTWRFSU");
    assertEquals(7, result.size());
  }

  @Test
  public void testParseAllDaysReverseOrder() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("USFRWTM");
    assertEquals(7, result.size());
  }

  @Test
  public void testParseRandomOrder() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("FRWTM");
    assertEquals(5, result.size());
    assertTrue(result.contains(Weekday.FRIDAY));
    assertTrue(result.contains(Weekday.THURSDAY));
    assertTrue(result.contains(Weekday.WEDNESDAY));
    assertTrue(result.contains(Weekday.TUESDAY));
    assertTrue(result.contains(Weekday.MONDAY));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWithLowerCaseInvalidCharacter() {
    CommandParserUtils.parseWeekdays("mxf");
  }

  @Test
  public void testParseReturnsSet() {
    Set<Weekday> result = CommandParserUtils.parseWeekdays("MTWRF");
    assertTrue(result instanceof Set);
  }

  @Test
  public void testParseSingleCharacterEachCase() {
    assertEquals(1, CommandParserUtils.parseWeekdays("m").size());
    assertEquals(1, CommandParserUtils.parseWeekdays("t").size());
    assertEquals(1, CommandParserUtils.parseWeekdays("w").size());
    assertEquals(1, CommandParserUtils.parseWeekdays("r").size());
    assertEquals(1, CommandParserUtils.parseWeekdays("f").size());
    assertEquals(1, CommandParserUtils.parseWeekdays("s").size());
    assertEquals(1, CommandParserUtils.parseWeekdays("u").size());
  }
}