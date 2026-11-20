package com.lemonlightmc.zenith.commands.argumentsbase;

import java.util.ArrayDeque;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException.CommandSyntaxExceptionContainer;

public class StringReader {
  private static final char SYNTAX_ESCAPE = '\\';
  private static final char SYNTAX_DOUBLE_QUOTE = '"';
  private static final char SYNTAX_SINGLE_QUOTE = '\'';

  private final String string;
  private int cursor = 0;
  private final ArrayDeque<Integer> points = new ArrayDeque<>();

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_START_OF_QUOTE = new CommandSyntaxExceptionContainer(
      "Expected quote to start a string");

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_END_OF_QUOTE = new CommandSyntaxExceptionContainer(
      "Unclosed quoted string");

  private static final CommandSyntaxExceptionContainer READER_INVALID_ESCAPE = new CommandSyntaxExceptionContainer(
      character -> "Invalid escape sequence '" + character + "' in quoted string");

  private static final CommandSyntaxExceptionContainer READER_INVALID_BOOL = new CommandSyntaxExceptionContainer(
      value -> "Invalid bool, expected true or false but found '" + value + "'");

  private static final CommandSyntaxExceptionContainer READER_INVALID_INT = new CommandSyntaxExceptionContainer(
      value -> "Invalid integer '" + value + "'");

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_INT = new CommandSyntaxExceptionContainer(
      "Expected integer");

  private static final CommandSyntaxExceptionContainer READER_INVALID_LONG = new CommandSyntaxExceptionContainer(
      value -> "Invalid long '" + value + "'");

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_LONG = new CommandSyntaxExceptionContainer(
      ("Expected long"));

  private static final CommandSyntaxExceptionContainer READER_INVALID_DOUBLE = new CommandSyntaxExceptionContainer(
      value -> "Invalid double '" + value + "'");

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_DOUBLE = new CommandSyntaxExceptionContainer(
      "Expected double");

  private static final CommandSyntaxExceptionContainer READER_INVALID_FLOAT = new CommandSyntaxExceptionContainer(
      value -> "Invalid float '" + value + "'");

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_FLOAT = new CommandSyntaxExceptionContainer(
      "Expected float");

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_BOOL = new CommandSyntaxExceptionContainer(
      "Expected bool");

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_RANGE = new CommandSyntaxExceptionContainer(
      "Expected Range");

  private static final CommandSyntaxExceptionContainer READER_EXPECTED_SYMBOL = new CommandSyntaxExceptionContainer(
      symbol -> "Expected '" + symbol + "'");

  public StringReader(final StringReader other) {
    if (other == null) {
      throw new IllegalArgumentException("Cant create a new StringReader from a null StringReader");
    }
    this.string = other.string == null ? "" : other.string;
    this.points.addAll(other.points);
    setCursor(other.cursor);
  }

  public StringReader(final String string) {
    this(string, 0);
  }

  public StringReader(final String string, final int cursor) {
    this.string = string == null ? "" : string;
    setCursor(cursor);
  }

  public static boolean isNumberStrict(final char c) {
    return c >= '0' && c <= '9';
  }

  public static boolean isNumber(final char c) {
    return c >= '0' && c <= '9' || c == '.' || c == '-';
  }

  public static boolean isAlphabet(final char c) {
    return c >= '0' && c <= '9'
        || c >= 'A' && c <= 'Z'
        || c >= 'a' && c <= 'z';
  }

  public static boolean isUnquotedString(final char c) {
    return c >= '0' && c <= '9'
        || c >= 'A' && c <= 'Z'
        || c >= 'a' && c <= 'z'
        || c == '_' || c == '-'
        || c == '.' || c == '+';
  }

  public static boolean isUnquotedString(final char c, final char delimiter) {
    return c >= '0' && c <= '9'
        || c >= 'A' && c <= 'Z'
        || c >= 'a' && c <= 'z'
        || c == '_' || c == '-'
        || c == '.' || c == '+' || c == delimiter;
  }

  public static boolean isQuote(final char c) {
    return c == SYNTAX_DOUBLE_QUOTE || c == SYNTAX_SINGLE_QUOTE;
  }

  public String getString() {
    return string;
  }

  public void setCursor(final int cursor) {
    if (cursor < 0) {
      this.cursor = 0;
    } else {
      this.cursor = Math.min(cursor, string.length());
    }
  }

  public int getCursor() {
    return cursor;
  }

  public void point() {
    points.push(cursor);
  }

  public void point(final int point) {
    points.push(point);
  }

  public void resetCursor() {
    if (points.isEmpty()) {
      return;
    }
    cursor = points.pop();
  }

  public int getPoint() {
    return points.isEmpty() ? 0 : points.peek();
  }

  public Integer[] getAllPoints() {
    return points.toArray(Integer[]::new);
  }

  public void revokePoint() {
    if (!points.isEmpty()) {
      points.pop();
    }
  }

  public int getRemainingLength() {
    return string.length() - cursor;
  }

  public int getTotalLength() {
    return string.length();
  }

  public String getRead() {
    return string.substring(0, cursor);
  }

  public String getRemaining() {
    return string.substring(cursor);
  }

  public String getSubString(final int start, final int end) {
    return string.substring(start, end);
  }

  public String getLastRead() {
    final int start = points.isEmpty() ? 0 : points.peek();
    final int safeStart = Math.max(0, Math.min(start, cursor));
    return string.substring(safeStart, cursor);
  }

  public boolean canRead(final int length) {
    return cursor + length <= string.length();
  }

  public boolean canRead() {
    return canRead(1);
  }

  public char peek() {
    return string.charAt(cursor);
  }

  public char peek(final int offset) {
    return string.charAt(cursor + offset);
  }

  public char read() {
    return string.charAt(cursor++);
  }

  public void skip() {
    cursor++;
  }

  public void skipWhitespace() {
    while (canRead() && Character.isWhitespace(peek())) {
      skip();
    }
  }

  public void expect(final char c) throws CommandSyntaxException {
    if (!canRead() || peek() != c) {
      throw READER_EXPECTED_SYMBOL.createWithContext(this,
          String.valueOf(c));
    }
    skip();
  }

  public int readInt() throws CommandSyntaxException {
    final int start = cursor;
    while (canRead() && isNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      throw READER_EXPECTED_INT.createWithContext(this);
    }
    try {
      return NumberConversions.parseInt(number);
    } catch (final NumberFormatException ex) {
      cursor = start;
      throw READER_INVALID_INT.createWithContext(this, number);
    }
  }

  public long readLong() throws CommandSyntaxException {
    final int start = cursor;
    while (canRead() && isNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      throw READER_EXPECTED_LONG.createWithContext(this);
    }
    try {
      return NumberConversions.parseLong(number);
    } catch (final NumberFormatException ex) {
      cursor = start;
      throw READER_INVALID_LONG.createWithContext(this, number);
    }
  }

  public double readDouble() throws CommandSyntaxException {
    final int start = cursor;
    while (canRead() && isNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      throw READER_EXPECTED_DOUBLE.createWithContext(this);
    }
    try {
      return NumberConversions.parseDouble(number);
    } catch (final NumberFormatException ex) {
      cursor = start;
      throw READER_INVALID_DOUBLE.createWithContext(this, number);
    }
  }

  public float readFloat() throws CommandSyntaxException {
    final int start = cursor;
    while (canRead() && isNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      throw READER_EXPECTED_FLOAT.createWithContext(this);
    }
    try {
      return NumberConversions.parseFloat(number);
    } catch (final NumberFormatException ex) {
      cursor = start;
      throw READER_INVALID_FLOAT.createWithContext(this, number);
    }
  }

  public boolean readBoolean() throws CommandSyntaxException {
    final int start = cursor;
    final String value = readString();
    if (value.isEmpty()) {
      cursor = start;
      throw READER_EXPECTED_BOOL.createWithContext(this);
    }

    if ("true".equals(value)) {
      return true;
    } else if ("false".equals(value)) {
      return false;
    } else {
      cursor = start;
      throw READER_INVALID_BOOL.createWithContext(this, value);
    }
  }

  public String readRange() throws CommandSyntaxException {
    final int start = cursor;
    while (canRead() && isNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      cursor = start;
      throw READER_EXPECTED_RANGE.createWithContext(this);
    }
    return number;
  }

  public String readUnquotedString() {
    final int start = cursor;
    while (canRead() && isUnquotedString(peek())) {
      skip();
    }
    return string.substring(start, cursor);
  }

  public String readQuotedString() throws CommandSyntaxException {
    if (!canRead()) {
      return "";
    }
    final char next = peek();
    if (!isQuote(next)) {
      throw READER_EXPECTED_START_OF_QUOTE.createWithContext(this);
    }
    skip();
    return readStringUntil(next);
  }

  public String readStringUntil(final char terminator) throws CommandSyntaxException {
    final StringBuilder result = new StringBuilder();
    boolean escaped = false;
    while (canRead()) {
      final char c = read();
      if (escaped) {
        if (c == terminator || c == SYNTAX_ESCAPE) {
          result.append(c);
          escaped = false;
        } else {
          setCursor(getCursor() - 1);
          throw READER_INVALID_ESCAPE.createWithContext(this,
              String.valueOf(c));
        }
      } else if (c == SYNTAX_ESCAPE) {
        escaped = true;
      } else if (c == terminator) {
        return result.toString();
      } else {
        result.append(c);
      }
    }

    throw READER_EXPECTED_END_OF_QUOTE.createWithContext(this);
  }

  public String readString() throws CommandSyntaxException {
    if (!canRead()) {
      return "";
    }
    final char next = peek();
    if (isQuote(next)) {
      skip();
      return readStringUntil(next);
    }
    return readUnquotedString();
  }

  public String[] readList(final char delimiter) throws CommandSyntaxException {
    if (!canRead()) {
      return new String[0];
    }
    final char next = peek();
    if (isQuote(next)) {
      skip();
      return readStringUntil(next).split("" + delimiter);
    }

    return readUnquotedList(delimiter);
  }

  public String[] readUnquotedList(final char delimiter) {
    final int start = cursor;
    while (canRead() && isUnquotedString(peek(), delimiter)) {
      skip();
    }
    return string.substring(start, cursor).split("" + delimiter);
  }

  @Override
  public int hashCode() {
    int result = 31 * (31 + string.hashCode()) + points.hashCode();
    return 31 * result + cursor;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final StringReader other = (StringReader) obj;
    return cursor == other.cursor
        && java.util.Objects.equals(string, other.string)
        && java.util.Objects.equals(points, other.points);
  }

  @Override
  public String toString() {
    return "StringReader [cursor=" + cursor + ", points=" + points + "]";
  }

}
