package com.augminish.app.index;

import static com.augminish.app.common.util.strings.StaticStringTest.ContentAssertion;
import static com.augminish.app.common.util.strings.StaticStringTest.HyperTextAssertion;
import static com.augminish.app.common.util.strings.StaticStringTest.WordFrequencyAssertion;

import com.augminish.app.common.util.mysql.MySQL;
import com.augminish.app.common.util.mysql.helper.SqlBuilder;
import com.augminish.app.common.util.object.PropertyHashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class IndexerTest {

    private static final Boolean isTesting = Boolean.TRUE;

    @BeforeClass
    public static void init() throws IOException {
        PropertyHashMap p = new PropertyHashMap("./.ignore/config-test.properties");
        Assert.assertTrue(p.contains("file.cache"));
    }

    @Test
    public void wordFrequencyTest() {

        StringBuilder text = new StringBuilder();
        text.append("com augminish app common ");
        text.append("util file app augminish com ");
        text.append("augminish app crawl index crawl ");
        text.append("object crawl app index object com ");
        text.append("com augminish app common common ");

        HashMap<String, Integer> wordFrequency = Indexer.getWordFrequency(text.toString());

        Assert.assertTrue(wordFrequency.containsKey("com"));
        Assert.assertEquals(4, wordFrequency.get("com").intValue());

        Assert.assertTrue(wordFrequency.containsKey("augminish"));
        Assert.assertEquals(4, wordFrequency.get("augminish").intValue());

        Assert.assertTrue(wordFrequency.containsKey("app"));
        Assert.assertEquals(5, wordFrequency.get("app").intValue());

        Assert.assertTrue(wordFrequency.containsKey("util"));
        Assert.assertEquals(1, wordFrequency.get("util").intValue());

        Assert.assertTrue(wordFrequency.containsKey("common"));
        Assert.assertEquals(3, wordFrequency.get("common").intValue());

        Assert.assertTrue(wordFrequency.containsKey("crawl"));
        Assert.assertEquals(3, wordFrequency.get("crawl").intValue());

        Assert.assertTrue(wordFrequency.containsKey("index"));
        Assert.assertEquals(2, wordFrequency.get("index").intValue());
    }

    @Test
    public void sanitizeText() {
        String testString = "This&& Test! String@ one# isn't$ supposed% to^ have& special* (characters)";
        String expected = "This Test String one isnt supposed to have special characters";

        Assert.assertEquals(expected, Indexer.sanitize(testString));
    }

    @Test
    public void indexSimulationTest() throws IOException, Exception {

        Indexer indexer = new Indexer(isTesting);

        MySQL mysql = new MySQL();
        mysql.use("AugminishTest");

        Assert.assertTrue(mysql.update(SqlBuilder.update("WebSitesIndex", "indexed").values("0").where("true").commit()));
        Assert.assertTrue(mysql.query("TRUNCATE WordFrequency;"));
        Assert.assertTrue(mysql.query("TRUNCATE HyperTexts;"));
        Assert.assertTrue(mysql.query("TRUNCATE Content;"));

        indexer.index();

        List<HashMap<String, Object>> hypertexts = mysql.select(SqlBuilder.select("HyperTexts", "tag", "webSiteId", "hasContent").commit());
        assertHyperTexts(hypertexts);

        List<HashMap<String, Object>> content = mysql.select(SqlBuilder.select("Content", "hyperTextId", "content").commit());
        assertContent(content);

        List<HashMap<String, Object>> wordFrequency = mysql.select(SqlBuilder.select("WordFrequency", "word", "frequency", "hyperTextId", "webSiteId")
                .where("webSiteId=1").commit());
        assertWordFrequency(wordFrequency);
    }

    private static void assertHyperTexts(List<HashMap<String, Object>> hypertexts) {

        int index = 0, tag = 0, webSiteId = 1, hasContent = 2;
        for (HashMap<String, Object> hypertext : hypertexts) {
            String[] values = HyperTextAssertion[index++].split(":");

            Assert.assertEquals(values[tag], hypertext.get("tag"));
            Assert.assertEquals(values[webSiteId], String.valueOf(hypertext.get("webSiteId")));
            Assert.assertEquals(values[hasContent], String.valueOf(hypertext.get("hasContent")));

            if (index >= HyperTextAssertion.length) {
                break;
            }
        }
    }

    private static void assertContent(List<HashMap<String, Object>> content) {

        int index = 0, hyperTextId = 0, contents = 1;
        for (HashMap<String, Object> c : content) {
            String[] values = ContentAssertion[index++].split(":", 2);

            Assert.assertEquals(values[hyperTextId], String.valueOf(c.get("hyperTextId")));
            Assert.assertEquals(values[contents], c.get("content"));
            if (index >= ContentAssertion.length) {
                break;
            }
        }
    }

    private static void assertWordFrequency(List<HashMap<String, Object>> wordFrequency) {

        HashMap<String, Boolean> wordFrequencyHashMap = fromListToHashMap(WordFrequencyAssertion);
        int index = 0;
        for (HashMap<String, Object> w : wordFrequency) {
            String key = createWordFrequencyKey(w);
            System.out.println(key);

            Assert.assertTrue(wordFrequencyHashMap.get(key));
            if (index >= WordFrequencyAssertion.length) {
                break;
            }
        }
    }

    private static HashMap<String, Boolean> fromListToHashMap(String[] list) {

        HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
        for (String entry : list) {
            hashMap.put(entry, Boolean.TRUE);
        }

        return hashMap;
    }

    private static String createWordFrequencyKey(HashMap<String, Object> wordFrequency) {
        StringBuilder wfk = new StringBuilder(wordFrequency.get("word").toString());
        wfk.append(":").append(wordFrequency.get("frequency")).append(":");
        wfk.append(wordFrequency.get("hyperTextId")).append(":");
        wfk.append(wordFrequency.get("webSiteId"));
        return wfk.toString();
    }
}