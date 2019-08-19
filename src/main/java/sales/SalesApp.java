package sales;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class SalesApp {
    private SalesDao salesDao;
    private SalesReportDao salesReportDao;
    private EcmService ecmService;

    SalesApp() {
        salesDao = new SalesDao();
        salesReportDao = new SalesReportDao();
        ecmService = new EcmService();
    }

    void generateSalesActivityReport(String salesId, int maxRow, boolean isNatTrade, boolean isSupervisor) {
        if (!isSalesIdValid(salesId)) {
            return;
        }
        Sales sales = getSalesBySalesId(salesId);
        if (isSalesOutOfEffectiveDate(sales)) {
            return;
        }
        List<SalesReportData> reportDataList = getSalesReportDataBySales(sales);
        List<SalesReportData> filteredReportDataList = getFilteredReportDataList(isSupervisor, reportDataList);
        filteredReportDataList = getLimitedSalesReportData(maxRow, reportDataList);
        List<String> headers = getReportHeaders(isNatTrade);
        SalesActivityReport report = this.generateReport(headers, reportDataList);
        uploadReportAsXml(report);

    }

    List<SalesReportData> getLimitedSalesReportData(int maxRow, List<SalesReportData> reportDataList) {
        // the origin logic was wrong and may case IndexOutOfBoundsException, so I change || to &&
        // the condition in the for-loop now is i < reportDataList.size() && i < maxRow
        return reportDataList.stream().limit(maxRow).collect(Collectors.toList());
    }

    List<SalesReportData> getFilteredReportDataList(boolean isSupervisor, List<SalesReportData> reportDataList) {
        return reportDataList.stream()
                .filter(salesReportData -> isSalesReportDataValid(isSupervisor, salesReportData))
                .collect(Collectors.toList());
    }

    boolean isSalesReportDataValid(boolean isSupervisor, SalesReportData data) {
        final String ALLOWED_TYPE = "SalesActivity";
        return ALLOWED_TYPE.equalsIgnoreCase(data.getType()) && (!data.isConfidential() || isSupervisor);
    }

    void uploadReportAsXml(SalesActivityReport report) {
        ecmService.uploadDocument(report.toXml());
    }

    List<String> getReportHeaders(boolean isNatTrade) {
        if (isNatTrade) {
            return Arrays.asList("Sales ID", "Sales Name", "Activity", "Time");
        }
        return Arrays.asList("Sales ID", "Sales Name", "Activity", "Local Time");
    }

    boolean isSalesIdValid(String salesId) {
        return salesId != null;
    }

    boolean isSalesOutOfEffectiveDate(Sales sales) {
        Date today = new Date();
        return today.after(sales.getEffectiveTo())
                || today.before(sales.getEffectiveFrom());
    }

    List<SalesReportData> getSalesReportDataBySales(Sales sales) {
        return salesReportDao.getReportData(sales);
    }

    Sales getSalesBySalesId(String salesId) {
        return salesDao.getSalesBySalesId(salesId);
    }

    SalesActivityReport generateReport(List<String> headers, List<SalesReportData> reportDataList) {
        // TODO Auto-generated method stub
        return null;
    }

}
