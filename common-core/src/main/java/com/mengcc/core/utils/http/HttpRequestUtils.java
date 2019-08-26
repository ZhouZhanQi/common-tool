package com.mengcc.core.utils.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author zhouzq
 * @date 2017-01-03
 */
@Slf4j
public class HttpRequestUtils {

    private static final String MICROSOFT_IE = "MSIE";
    private static final int IE_URL_LIMIT = 150;

    /**
     * localhost的IP地址(ipv4)
     */
    private static final String LOCALHOST_IP_V4 = "127.0.0.1";

    /**
     * localhost的IP地址(ipv6)
     */
    private static final String LOCALHOST_IP_V6 = "0:0:0:0:0:0:0:1";

    private static final String UNKNOWN = "unknown";
    private static final String COMMA = ",";
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"};

    /**
     * 获取请求的客户端ip地址
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = null;
        // 按顺序尝试从http头获取
        for (String header : HEADERS_TO_TRY) {
            ip = request.getHeader(header);
            if (StringUtils.isNotBlank(ip) && !StringUtils.equalsIgnoreCase(ip, UNKNOWN)) {
                // 找到IP, 跳出循环
                break;
            }
        }
        // 从http头无法获取, 直接使用 getRemoteAddr
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(ip, UNKNOWN)) {
            ip = request.getRemoteAddr();
        }
        // 如果经过多次转发, IP可能是一个列表, 默认第一个是客户端ip
        if (StringUtils.isNotBlank(ip) && StringUtils.indexOf(ip, COMMA) > 0) {
            String[] ipArray = StringUtils.split(ip, COMMA);
            ip = ipArray[0];
        }
        return ip;
    }

    /**
     * 获取当前URL+Parameter
     *
     * @param request
     * @return 返回完整URL
     */
    public static String getRequestUrl(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String queryString = req.getQueryString();

        queryString = StringUtils.isBlank(queryString) ? "" : "?" + queryString;
        return req.getRequestURI() + queryString;
    }

    /**
     * 判断请求是否为Ajax请求
     *
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null
                && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    /**
     * 判断一个ip是否为localhost的ip(仅检查默认的ipv4和ipv6地址)
     * @param ip
     * @return
     */
    public static boolean isLocalhostIp(String ip) {
        return LOCALHOST_IP_V6.equals(ip) || LOCALHOST_IP_V4.equals(ip);
    }

    /**
     * 根据不同浏览器将文件名中的汉字转为UTF8编码的串,以便下载时能正确显示另存的文件名.
     *
     * @param request
     * @param sourcename 原文件名
     * @return 重新编码后的文件名
     */
    public static String encodeFileName(HttpServletRequest request, final String sourcename) {
        String agent = request.getHeader("User-Agent");
        String result;
        try {
            boolean isFireFox = (agent != null && agent.toLowerCase().contains("firefox"));
            if (isFireFox) {
                result = new String(sourcename.getBytes("UTF-8"), "ISO8859-1");
            } else {
                result = URLEncoder.encode(sourcename, "UTF-8");
                if ((agent != null && agent.contains(MICROSOFT_IE))) {
                    // see http://support.microsoft.com/default.aspx?kbid=816868
                    if (result.length() > IE_URL_LIMIT) {
                        // 根据request的locale 得出可能的编码
                        result = new String(result.getBytes("UTF-8"), "ISO8859-1");
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(">> encode filename[" + sourcename + "] error", e);
            result = sourcename;
        }
        return result;
    }
}
