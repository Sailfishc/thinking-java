package examples.dom4j;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-7-23
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates
 */
public class ParsingXml {
    public Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }
}
