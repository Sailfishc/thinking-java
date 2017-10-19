package examples.httpclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 利用JSouup解析
 *
 * @author XiongNeng
 * @version 1.0
 * @since 2015/2/4
 */
public class JSoupUtil {
    private static final Logger _log = LoggerFactory.getLogger(JSoupUtil.class);
    /**
     * 获取链接
     * @param htmlContent html
     * @return 链接
     * @throws Exception
     */
    public static String parseLink(String htmlContent) throws Exception {
        // _log.info(htmlContent);
        String result = null;
        if (htmlContent == null) return null;
        if (htmlContent.contains("暂未查询到相关记录")) {
            return "404";
        }
        Document doc = Jsoup.parse(htmlContent);
        Elements content = doc.getElementsByAttributeValue("class", "font16");
        if (content.size() > 0) {
            Element e = content.get(0);
            // _log.info("获取链接href=" + e.child(0).attr("href"));
            result = e.child(0).attr("href");
            if (!result.startsWith("http://")) {
                _log.info("获取的相对链接href=" + result);
                result = QueryManager.getInstance().getHomepageUrl() + result.substring(3);
            }
        }
        return result;
    }

    /**
     * 获取真实营业场所或地址
     * @param htmlContent html
     * @return 营业场所或地址
     * @throws Exception
     */
    public static String parseLocation(String htmlContent) throws Exception {
        Document doc = Jsoup.parse(htmlContent);
        Element location = doc.select(
                "table[class=detailsList]:eq(0) > tbody > tr >th:matches(所$):eq(0) + td").first();
        String result = location.text();
        _log.info("location=" + result);
        return result;
    }

    /**
     * 获取企业完整信息
     * @param htmlContent html
     * @throws Exception
     */
    public static void parseCompany(String htmlContent, String investorHtml, Company c) throws Exception {
        Document doc = Jsoup.parse(htmlContent);
        // 企业名
        Element nameE = doc.select(
                "table[class=detailsList]:eq(0) > tbody > tr >th:matches(^名称$) + td").first();
        if (nameE != null) c.setCompanyName(nameE.text());
        // 注册号
        Element taxnoE = doc.select(
                "table[class=detailsList]:eq(0) > tbody > tr >th:matches(^注册号$) + td").first();
        if (taxnoE != null) c.setTaxno(taxnoE.text());
        // 法定代表人
        Element lawPersonE = doc.select(
                "table[class=detailsList]:eq(0) > tbody > tr >th:matches(^法定代表人|负责人|投资人|经营者$) + td").first();
        if (lawPersonE != null) c.setLawPerson(lawPersonE.text());
        // 成立日期
        Element regDateE = doc.select(
                "table[class=detailsList]:eq(0) > tbody > tr >th:matches(^成立日期|注册日期$) + td").first();
        if (regDateE != null) c.setRegDate(DateUtil.toDate(regDateE.text()));
        // 住所
        Element location = doc.select(
                "table[class=detailsList]:eq(0) > tbody > tr >th:matches(所$) + td").first();
        if (location != null) c.setLocation(location.text());
        // 经营范围
        Element business = doc.select(
                "table[class=detailsList]:eq(0) > tbody > tr >th:matches(^经营范围$) + td").first();
        if (business != null) c.setBusiness(business.text());
        // 股东/发起人，这里需要异步再次发起一次请求
        c.setStockholder(fetchInvestor(investorHtml));
        // 登记状态
        Element status = doc.select(
                "table[class=detailsList]:eq(0) > tbody > tr >th:matches(^登记状态$) + td").first();
        if (business != null) {
            String statuss = status.text();
            c.setStatus(statuss);
            if (!"存续".equals(statuss)) {
                c.setResultType(2);  // 已经无效了
            }
        }
    }

    private static String fetchInvestor(String investorHtml) {
        if (StringUtil.isBlank(investorHtml)) return null;
        if (investorHtml.contains("<html") && investorHtml.contains("touziren")) {
            Document doc = Jsoup.parse(investorHtml);
            // 股东/发起人
            Elements nameE = doc.select("table#touziren tr >td:eq(1)");
            if (nameE != null && nameE.size() > 0) {
                StringBuilder sbb = new StringBuilder();
                for (Element e : nameE) {
                    sbb.append(e.text()).append("/");
                }
                String sbb1 = sbb.toString();
                return sbb1.substring(0, sbb1.length() - 1);
            } else {
                return "";
            }
        }
        JSONObject root = JSON.parseObject(investorHtml);
        JSONArray array = root.getJSONArray("investorList");
        StringBuilder sb = new StringBuilder();
        for(Object o: array) {
            JSONObject json = (JSONObject)o;
            sb.append(json.getString("inv")).append("/");
        }
        String sbs = sb.toString();
        if (sbs.length() > 0) return sbs.substring(0, sbs.length() - 1);
        return null;
    }

//    private static String parseLinkXpath(String htmlContent) throws Exception {
//        InputSource source = new InputSource(new StringReader(htmlContent));
//
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        Document document = db.parse(source);
//
//        XPathFactory xpathFactory = XPathFactory.newInstance();
//        XPath xpath = xpathFactory.newXPath();
//
//        String link = xpath.evaluate("//div[@class='list'][1]/ul/li[1]/a/@href", document);
//
//        _log.info("link=" + link);
//        return link;
//    }

    public static void main(String[] args) throws Exception{
        String investorHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                "<table  cellpadding=\"0\" cellspacing=\"0\" class=\"detailsList\" id=\"touziren\" style=\"\" >\n" +
                "           <tr>\n" +
                "               <td width=\"20%\" style=\"text-align:center;\">自然人股东</td>\n" +
                "               <td width=\"20%\" style=\"text-align:center;\">李芳生</td>\n" +
                "               <td width=\"25%\" style=\"text-align:center;\"></td>\n" +
                "               <td width=\"25%\" style=\"text-align:center;\">不公示</td>\n" +
                "               <td width=\"10%\" style=\"text-align:center;\">\n" +
                "                        <a href=\"#\" onclick=\"return alert('该公司的股东及出资信息在2014年3月1日后发生变化的,股东详情企业自主公示');\">详情</a>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "           <tr>\n" +
                "               <td width=\"20%\" style=\"text-align:center;\">自然人股东</td>\n" +
                "               <td width=\"20%\" style=\"text-align:center;\">李晓毅</td>\n" +
                "               <td width=\"25%\" style=\"text-align:center;\"></td>\n" +
                "               <td width=\"25%\" style=\"text-align:center;\">不公示</td>\n" +
                "               <td width=\"10%\" style=\"text-align:center;\">\n" +
                "                        <a href=\"#\" onclick=\"return alert('该公司的股东及出资信息在2014年3月1日后发生变化的,股东详情企业自主公示');\">详情</a>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "    </table>\n" +
                "</html>";
        // todo
        if (investorHtml.contains("<html") && investorHtml.contains("touziren")) {
            Document doc = Jsoup.parse(investorHtml);
            // 股东/发起人
            Elements nameE = doc.select("table#touziren tr >td:eq(1)");
            if (nameE != null && nameE.size() > 0) {
                StringBuilder sbb = new StringBuilder();
                for (Element e : nameE) {
                    sbb.append(e.text()).append("/");
                }
                String sbb1 = sbb.toString();
                System.out.println(sbb1.substring(0, sbb1.length() - 1));
            }
        }
//        DataInputStream dis = new DataInputStream(new FileInputStream(
//                "D:\\work\\projects\\thinking-java\\src\\main\\resources\\test.html"));
//        byte[] datainBytes = new byte[dis.available()];
//        dis.readFully(datainBytes);
//        dis.close();
//        String content = new String(datainBytes, 0, datainBytes.length);
////        _log.info(content);
//
//        parseLocation(content);
    }
}
