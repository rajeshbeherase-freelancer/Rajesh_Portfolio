package Hrusikesh;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Hrusi {

    private WebDriver driver;

    private static final String URL = "https://transfunnel.com/";

    // Initial thresholds - you can tune these later
    private static final double NORMAL_PAGE_LOAD = 3000; // 3 sec
    private static final double SLOW_PAGE_LOAD = 5000;   // 5 sec

    private static final double NORMAL_TTFB = 800;       // 800 ms
    private static final double SLOW_TTFB = 1800;        // 1.8 sec

    private static final double NORMAL_LCP = 2500;       // 2.5 sec
    private static final double SLOW_LCP = 4000;         // 4 sec


    @BeforeMethod
    public void setup() {

        ChromeOptions options = new ChromeOptions();

        // Required for Jenkins/server environment
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);

        driver.manage()
                .timeouts()
                .pageLoadTimeout(Duration.ofSeconds(30));
    }


    @Test
    public void checkTransfunnelWebsitePerformance() {

        System.out.println();
        System.out.println("=================================================");
        System.out.println("       TRANSFUNNEL WEBSITE PERFORMANCE TEST");
        System.out.println("=================================================");

        long seleniumStart = System.currentTimeMillis();

        int httpStatus = 0;

        try {

            driver.get(URL);

        } catch (Exception e) {

            System.out.println(
                    "WARNING: Page load timeout/exception: "
                            + e.getMessage()
            );
        }

        long seleniumLoadTime =
                System.currentTimeMillis() - seleniumStart;


        JavascriptExecutor js =
                (JavascriptExecutor) driver;


        /*
         * Give the browser a short amount of time
         * to populate paint/LCP metrics.
         */
        try {

            Thread.sleep(1000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }


        /*
         * ==========================================
         * NAVIGATION TIMING
         * ==========================================
         */

        Map<String, Object> navigation =
                (Map<String, Object>) js.executeScript(
                        """
                        const nav =
                            performance.getEntriesByType('navigation')[0];

                        return {
                            dnsStart: nav.domainLookupStart,
                            dnsEnd: nav.domainLookupEnd,

                            connectStart: nav.connectStart,
                            connectEnd: nav.connectEnd,

                            requestStart: nav.requestStart,
                            responseStart: nav.responseStart,

                            domInteractive: nav.domInteractive,
                            domComplete: nav.domComplete,

                            loadEventEnd: nav.loadEventEnd,

                            transferSize: nav.transferSize,

                            responseStatus: nav.responseStatus
                        };
                        """
                );


        double dnsStart =
                getDouble(navigation, "dnsStart");

        double dnsEnd =
                getDouble(navigation, "dnsEnd");

        double connectStart =
                getDouble(navigation, "connectStart");

        double connectEnd =
                getDouble(navigation, "connectEnd");

        double requestStart =
                getDouble(navigation, "requestStart");

        double responseStart =
                getDouble(navigation, "responseStart");


        double domInteractive =
                getDouble(navigation, "domInteractive");

        double domComplete =
                getDouble(navigation, "domComplete");


        httpStatus =
                (int) getDouble(
                        navigation,
                        "responseStatus"
                );


        /*
         * ==========================================
         * CALCULATE NAVIGATION METRICS
         * ==========================================
         */

        double dnsTime =
                dnsEnd - dnsStart;

        double connectionTime =
                connectEnd - connectStart;

        double serverResponseTime =
                responseStart - requestStart;

        /*
         * responseStart is measured from the
         * beginning of navigation.
         *
         * This gives us an approximation of TTFB.
         */
        double ttfb =
                responseStart;


        /*
         * Selenium load time is useful because
         * it represents what Selenium actually
         * experienced.
         */
        double pageLoad =
                seleniumLoadTime;

        /*
         * ==========================================
         * FIRST CONTENTFUL PAINT
         * ==========================================
         */

        Map<String, Object> paint =
                (Map<String, Object>) js.executeScript(
                        """
                        const entries =
                            performance.getEntriesByType('paint');

                        let fcp = 0;

                        for (const entry of entries) {

                            if (entry.name ===
                                'first-contentful-paint') {

                                fcp = entry.startTime;
                            }
                        }

                        return {
                            fcp: fcp
                        };
                        """
                );


        double fcp =
                getDouble(paint, "fcp");


        /*
         * ==========================================
         * LARGEST CONTENTFUL PAINT
         * ==========================================
         */

        Number lcpValue =
                (Number) js.executeScript(
                        """
                        const entries =
                            performance.getEntriesByType(
                                'largest-contentful-paint'
                            );

                        if (entries.length === 0) {
                            return 0;
                        }

                        return entries[
                            entries.length - 1
                        ].startTime;
                        """
                );


        double lcp =
                lcpValue.doubleValue();


        /*
         * ==========================================
         * RESOURCE INFORMATION
         * ==========================================
         */

        Map<String, Object> resources =
                (Map<String, Object>) js.executeScript(
                        """
                        const entries =
                            performance.getEntriesByType('resource');

                        let transferSize = 0;

                        let failedResources = 0;

                        entries.forEach(function(resource) {

                            transferSize +=
                                resource.transferSize || 0;

                            /*
                             * responseStatus is available
                             * for resource timing entries
                             * in supported browsers.
                             */
                            if (
                                resource.responseStatus >= 400
                            ) {
                                failedResources++;
                            }
                        });

                        return {
                            count: entries.length,
                            transferSize: transferSize,
                            failedResources: failedResources
                        };
                        """
                );


        long resourceCount =
                getLong(resources, "count");


        long transferSize =
                getLong(resources, "transferSize");


        long failedResources =
                getLong(resources, "failedResources");


        /*
         * ==========================================
         * DETERMINE WEBSITE HEALTH
         * ==========================================
         */

        String health;

        if (
                pageLoad >= SLOW_PAGE_LOAD
                        || ttfb >= SLOW_TTFB
                        || lcp >= SLOW_LCP
        ) {

            health = "CRITICAL";

        } else if (
                pageLoad >= NORMAL_PAGE_LOAD
                        || ttfb >= NORMAL_TTFB
                        || lcp >= NORMAL_LCP
        ) {

            health = "SLOW";

        } else {

            health = "NORMAL";
        }


        /*
         * ==========================================
         * HEALTH SCORE
         * ==========================================
         *
         * Simple score from 0-100.
         *
         * This is designed for non-technical users.
         */

        int healthScore =
                calculateHealthScore(
                        pageLoad,
                        ttfb,
                        lcp
                );


        String timestamp =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"
                                )
                        );


        /*
         * ==========================================
         * PRINT RESULTS
         * ==========================================
         */

        System.out.println();

        System.out.println(
                "Timestamp              : " + timestamp
        );

        System.out.println(
                "Website                : " + URL
        );

        System.out.println(
                "-------------------------------------------------"
        );

        System.out.println(
                "HTTP Status             : " + httpStatus
        );

        System.out.println(
                "Page Load               : "
                        + format(pageLoad)
                        + " ms"
        );

        System.out.println(
                "TTFB                    : "
                        + format(ttfb)
                        + " ms"
        );

        System.out.println(
                "Server Response        : "
                        + format(serverResponseTime)
                        + " ms"
        );

        System.out.println(
                "DNS                     : "
                        + format(dnsTime)
                        + " ms"
        );

        System.out.println(
                "Connection              : "
                        + format(connectionTime)
                        + " ms"
        );

        System.out.println(
                "FCP                     : "
                        + format(fcp)
                        + " ms"
        );

        System.out.println(
                "LCP                     : "
                        + format(lcp)
                        + " ms"
        );

        System.out.println(
                "DOM Interactive         : "
                        + format(domInteractive)
                        + " ms"
        );

        System.out.println(
                "DOM Complete            : "
                        + format(domComplete)
                        + " ms"
        );

        System.out.println(
                "Resources               : "
                        + resourceCount
        );

        System.out.println(
                "Page Size               : "
                        + formatBytes(transferSize)
        );

        System.out.println(
                "Failed Resources        : "
                        + failedResources
        );

        System.out.println(
                "-------------------------------------------------"
        );

        System.out.println(
                "HEALTH SCORE            : "
                        + healthScore
                        + " / 100"
        );

        System.out.println(
                "WEBSITE HEALTH          : "
                        + getHealthEmoji(health)
                        + " "
                        + health
        );

        System.out.println(
                "================================================="
        );


        /*
         * ==========================================
         * GENERATE HTML REPORT
         * ==========================================
         */

        generateHtmlReport(
                timestamp,
                httpStatus,
                pageLoad,
                ttfb,
                serverResponseTime,
                dnsTime,
                connectionTime,
                fcp,
                lcp,
                domInteractive,
                domComplete,
                resourceCount,
                transferSize,
                failedResources,
                health,
                healthScore
        );


        /*
         * ==========================================
         * SAVE HISTORICAL CSV DATA
         * ==========================================
         */

        saveCsv(
                timestamp,
                httpStatus,
                pageLoad,
                ttfb,
                fcp,
                lcp,
                resourceCount,
                transferSize,
                failedResources,
                health,
                healthScore
        );


        /*
         * Don't fail merely because the site is slow.
         *
         * SLOW/CRITICAL should be recorded as a
         * monitoring condition.
         *
         * Fail only if the website itself isn't
         * responding correctly.
         */

        Assert.assertTrue(
                httpStatus >= 200
                        && httpStatus < 500,
                "Website returned unexpected HTTP status: "
                        + httpStatus
        );
    }


    /*
     * ==========================================
     * HEALTH SCORE
     * ==========================================
     */

    private int calculateHealthScore(
            double pageLoad,
            double ttfb,
            double lcp) {

        int score = 100;


        /*
         * Page load penalty
         */

        if (pageLoad > 3000) {

            score -= 20;
        }

        if (pageLoad > 5000) {

            score -= 30;
        }


        /*
         * TTFB penalty
         */

        if (ttfb > 800) {

            score -= 15;
        }

        if (ttfb > 1800) {

            score -= 25;
        }


        /*
         * LCP penalty
         */

        if (lcp > 2500) {

            score -= 15;
        }

        if (lcp > 4000) {

            score -= 25;
        }


        if (score < 0) {

            score = 0;
        }


        return score;
    }


    /*
     * ==========================================
     * HTML REPORT
     * ==========================================
     */

    private void generateHtmlReport(
            String timestamp,
            int httpStatus,
            double pageLoad,
            double ttfb,
            double serverResponseTime,
            double dnsTime,
            double connectionTime,
            double fcp,
            double lcp,
            double domInteractive,
            double domComplete,
            long resourceCount,
            long transferSize,
            long failedResources,
            String health,
            int healthScore) {

        try {

            File directory =
                    new File("reports");

            if (!directory.exists()) {

                directory.mkdirs();
            }


            File reportFile =
                    new File(
                            "reports/performance-report.html"
                    );


            try (PrintWriter writer =
                         new PrintWriter(
                                 new FileWriter(
                                         reportFile
                                 )
                         )) {


                writer.println(
                        "<!DOCTYPE html>"
                );

                writer.println(
                        "<html>"
                );

                writer.println(
                        "<head>"
                );

                writer.println(
                        "<title>Transfunnel Performance Report</title>"
                );

                writer.println(
                        "<meta charset='UTF-8'>"
                );


                writer.println(
                        "<style>"
                                + "body{font-family:Arial;"
                                + "margin:40px;"
                                + "background:#f5f5f5;}"
                                + ".container{max-width:900px;"
                                + "margin:auto;"
                                + "background:white;"
                                + "padding:30px;"
                                + "border-radius:10px;}"
                                + ".health{font-size:28px;"
                                + "font-weight:bold;"
                                + "padding:20px;"
                                + "border-radius:8px;"
                                + "margin:20px 0;}"
                                + ".normal{background:#d4edda;"
                                + "color:#155724;}"
                                + ".slow{background:#fff3cd;"
                                + "color:#856404;}"
                                + ".critical{background:#f8d7da;"
                                + "color:#721c24;}"
                                + "table{width:100%;"
                                + "border-collapse:collapse;}"
                                + "th,td{padding:12px;"
                                + "border:1px solid #ddd;}"
                                + "th{text-align:left;}"
                                + "</style>"
                );


                writer.println(
                        "</head>"
                );

                writer.println(
                        "<body>"
                );

                writer.println(
                        "<div class='container'>"
                );


                writer.println(
                        "<h1>Transfunnel Website "
                                + "Performance Report</h1>"
                );


                writer.println(
                        "<p><b>Checked:</b> "
                                + timestamp
                                + "</p>"
                );


                String cssClass =
                        health.toLowerCase();


                writer.println(
                        "<div class='health "
                                + cssClass
                                + "'>"
                );


                writer.println(
                        getHealthEmoji(health)
                                + " Website Health: "
                                + health
                );


                writer.println(
                        "<br>"
                                + "<small>Health Score: "
                                + healthScore
                                + " / 100</small>"
                );


                writer.println(
                        "</div>"
                );


                writer.println(
                        "<table>"
                );


                writer.println(
                        "<tr>"
                                + "<th>Metric</th>"
                                + "<th>Value</th>"
                                + "</tr>"
                );


                htmlRow(
                        writer,
                        "HTTP Status",
                        String.valueOf(httpStatus)
                );

                htmlRow(
                        writer,
                        "Page Load",
                        format(pageLoad) + " ms"
                );

                htmlRow(
                        writer,
                        "TTFB",
                        format(ttfb) + " ms"
                );

                htmlRow(
                        writer,
                        "Server Response",
                        format(serverResponseTime) + " ms"
                );

                htmlRow(
                        writer,
                        "DNS",
                        format(dnsTime) + " ms"
                );

                htmlRow(
                        writer,
                        "Connection",
                        format(connectionTime) + " ms"
                );

                htmlRow(
                        writer,
                        "First Contentful Paint",
                        format(fcp) + " ms"
                );

                htmlRow(
                        writer,
                        "Largest Contentful Paint",
                        format(lcp) + " ms"
                );

                htmlRow(
                        writer,
                        "DOM Interactive",
                        format(domInteractive) + " ms"
                );

                htmlRow(
                        writer,
                        "DOM Complete",
                        format(domComplete) + " ms"
                );

                htmlRow(
                        writer,
                        "Resources",
                        resourceCount + " files"
                );

                htmlRow(
                        writer,
                        "Page Size",
                        formatBytes(transferSize)
                );

                htmlRow(
                        writer,
                        "Failed Resources",
                        String.valueOf(
                                failedResources
                        )
                );


                writer.println(
                        "</table>"
                );


                writer.println(
                        "<h2>What does this mean?</h2>"
                );


                writer.println(
                        "<p>"
                                + getHealthMessage(health)
                                + "</p>"
                );


                writer.println(
                        "<h3>Thresholds</h3>"
                );


                writer.println(
                        "<ul>"
                                + "<li>Normal Page Load: &lt; 3 sec</li>"
                                + "<li>Slow Page Load: 3-5 sec</li>"
                                + "<li>Critical Page Load: &gt; 5 sec</li>"
                                + "<li>Normal TTFB: &lt; 800 ms</li>"
                                + "<li>Normal LCP: &lt; 2.5 sec</li>"
                                + "</ul>"
                );


                writer.println(
                        "</div>"
                );

                writer.println(
                        "</body>"
                );

                writer.println(
                        "</html>"
                );
            }


            System.out.println(
                    "HTML report created at: "
                            + reportFile.getAbsolutePath()
            );


        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private void htmlRow(
            PrintWriter writer,
            String name,
            String value) {

        writer.println(
                "<tr>"
                        + "<td><b>"
                        + name
                        + "</b></td>"
                        + "<td>"
                        + value
                        + "</td>"
                        + "</tr>"
        );
    }


    /*
     * ==========================================
     * CSV HISTORY
     * ==========================================
     */

    private void saveCsv(
            String timestamp,
            int httpStatus,
            double pageLoad,
            double ttfb,
            double fcp,
            double lcp,
            long resourceCount,
            long transferSize,
            long failedResources,
            String health,
            int healthScore) {

        try {

            File directory =
                    new File("reports");

            if (!directory.exists()) {

                directory.mkdirs();
            }


            File csvFile =
                    new File(
                            "reports/performance-history.csv"
                    );


            boolean fileExists =
                    csvFile.exists();


            try (FileWriter writer =
                         new FileWriter(
                                 csvFile,
                                 true
                         )) {


                /*
                 * Add header only once.
                 */

                if (!fileExists) {

                    writer.append(
                            "Timestamp,"
                                    + "HTTP_Status,"
                                    + "Page_Load_ms,"
                                    + "TTFB_ms,"
                                    + "FCP_ms,"
                                    + "LCP_ms,"
                                    + "Resources,"
                                    + "Transfer_Bytes,"
                                    + "Failed_Resources,"
                                    + "Health,"
                                    + "Health_Score\n"
                    );
                }


                writer.append(
                        timestamp
                                + ","
                                + httpStatus
                                + ","
                                + pageLoad
                                + ","
                                + ttfb
                                + ","
                                + fcp
                                + ","
                                + lcp
                                + ","
                                + resourceCount
                                + ","
                                + transferSize
                                + ","
                                + failedResources
                                + ","
                                + health
                                + ","
                                + healthScore
                                + "\n"
                );
            }


            System.out.println(
                    "Historical CSV updated."
            );


        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    /*
     * ==========================================
     * HELPER METHODS
     * ==========================================
     */

    private double getDouble(
            Map<String, Object> map,
            String key) {

        Object value =
                map.get(key);

        if (value == null) {

            return 0;
        }

        return ((Number) value).doubleValue();
    }


    private long getLong(
            Map<String, Object> map,
            String key) {

        Object value =
                map.get(key);

        if (value == null) {

            return 0;
        }

        return ((Number) value).longValue();
    }


    private String format(
            double value) {

        return String.format(
                "%.0f",
                value
        );
    }


    private String formatBytes(
            long bytes) {

        if (bytes < 1024) {

            return bytes + " B";
        }

        if (bytes < 1024 * 1024) {

            return String.format(
                    "%.2f KB",
                    bytes / 1024.0
            );
        }

        return String.format(
                "%.2f MB",
                bytes / (1024.0 * 1024.0)
        );
    }


    private String getHealthEmoji(
            String health) {

        switch (health) {

            case "NORMAL":
                return "🟢";

            case "SLOW":
                return "🟠";

            case "CRITICAL":
                return "🔴";

            default:
                return "⚪";
        }
    }


    private String getHealthMessage(
            String health) {

        switch (health) {

            case "NORMAL":

                return "Website performance is "
                        + "within the configured normal "
                        + "range.";

            case "SLOW":

                return "Website performance has "
                        + "degraded. It should be "
                        + "monitored.";

            case "CRITICAL":

                return "Website is significantly "
                        + "slow. This could negatively "
                        + "affect the landing-page "
                        + "user experience.";

            default:

                return "Performance status is unknown.";
        }
    }


    @AfterMethod
    public void tearDown() {

        if (driver != null) {

            driver.quit();
        }
    }
}
