package org.nutz.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringsTest {

	@Test
	public void testIsQuoteByIgnoreBlank() {
		assertTrue(Strings.isQuoteByIgnoreBlank("[AB]", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("[]", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("   []", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("[]   ", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("  [  AB  ]   ", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("  [  AB  ]", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("[  AB  ]   ", '[', ']'));

		assertFalse(Strings.isQuoteByIgnoreBlank(null, '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("", '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("[AB", '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("   [AB", '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("AB]", '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("AB]   ", '[', ']'));

	}

	@Test
	public void testIsQuoteBy() {
		assertTrue(Strings.isQuoteBy("[AB]", '[', ']'));

		assertFalse(Strings.isQuoteBy(null, '[', ']'));
		assertFalse(Strings.isQuoteBy("   ", '[', ']'));
		assertFalse(Strings.isQuoteBy("", '[', ']'));
		assertFalse(Strings.isQuoteBy("[", '[', ']'));
		assertFalse(Strings.isQuoteBy("[AB", '[', ']'));
		assertFalse(Strings.isQuoteBy("AB]", '[', ']'));
	}

	@Test
	public void testRemoveFirst() {
		assertNull(Strings.removeFirst(null));
		assertEquals("2345", Strings.removeFirst("12345"));
		assertEquals("", Strings.removeFirst(""));
		assertEquals("", Strings.removeFirst("A"));
	}

	@Test
	public void testRemoveFirst2() {
		assertNull(Strings.removeFirst(null, 'A'));
		assertEquals("BCD", Strings.removeFirst("ABCD", 'A'));
		assertEquals("", Strings.removeFirst("", 'A'));
		assertEquals("", Strings.removeFirst("A", 'A'));
		assertEquals("ABCD", Strings.removeFirst("ABCD", 'B'));
	}

	@Test
	public void testUpperWord() {
		assertEquals("", Strings.upperWord("-", '-'));
		assertEquals("", Strings.upperWord("---", '-'));
		assertEquals("aBCD", Strings.upperWord("a-b-c-d", '-'));
		assertEquals("helloWorld", Strings.upperWord("hello-world", '-'));
	}

	@Test
	public void testLowerWord() {
		assertEquals("", Strings.lowerFirst(""));
		assertEquals("aCV", Strings.lowerFirst("aCV"));
		assertEquals("eee", Strings.lowerFirst("eee"));
		assertEquals("vCD", Strings.lowerFirst("VCD"));
		assertEquals("vff", Strings.lowerFirst("Vff"));
	}

	@Test
	public void test_split_ignore_blank() {
		assertArrayEquals(null, Strings.splitIgnoreBlank(null));
		assertArrayEquals(new String[]{}, Strings.splitIgnoreBlank(" "));
		assertArrayEquals(new String[]{"2", "3", "5"}, Strings.splitIgnoreBlank("2,3,, 5"));
		assertArrayEquals(new String[]{"2", "3", "5", "6"}, Strings.splitIgnoreBlank("2,3,, 5,6,"));
		assertArrayEquals(new String[]{"2,3,5,6,"}, Strings.splitIgnoreBlank("2,3,5,6,", ",,"));
		assertArrayEquals(new String[]{"2,3", "5", "6,"}, Strings.splitIgnoreBlank("2,3 ,,5,,6,", ",,"));
		assertArrayEquals(new String[]{"2,3", "5", "6,"}, Strings.splitIgnoreBlank("2,3,,5 ,,,,6,", ",,"));
	}
}
