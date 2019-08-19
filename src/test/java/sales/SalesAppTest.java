package sales;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalesAppTest {

    @InjectMocks
    SalesApp salesApp;
    @Mock
    SalesDao salesDao;
    @Mock
    SalesReportDao salesReportDao;
    @Mock
    EcmService ecmService;

    @Test
    public void testIsSalesIdValid_givenSalesWithIdNull_thenReturnFalse() {
        String wrongId = null;

        boolean salesIdValid = salesApp.isSalesIdValid(wrongId);

        assertFalse(salesIdValid);
    }

    @Test
    public void testIsSalesIdValid_givenSalesWithIdNotNull_thenReturnTrue() {
        String acceptableId = "I123";

        boolean salesIdValid = salesApp.isSalesIdValid(acceptableId);

        assertTrue(salesIdValid);
    }

    @Test
    public void testIsSalesOutOfEffectiveDate_givenEffectiveStartDateIsYesterdayAndEffectiveEndDateIsTomorrow_thenReturnFalse() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        Sales sales = mock(Sales.class);
        when(sales.getEffectiveFrom()).thenReturn(yesterday.getTime());
        when(sales.getEffectiveTo()).thenReturn(tomorrow.getTime());

        boolean isOutOfEffectiveDate = salesApp.isSalesOutOfEffectiveDate(sales);

        assertFalse(isOutOfEffectiveDate);
    }

    @Test
    public void testIsSalesOutOfEffectiveDate_givenEffectiveStartDateIsTheDayBeforeYesterdayAndEffectiveEndDateIsYesterday_thenReturnTrue() {
        Calendar theDayBeforeYesterday = Calendar.getInstance();
        theDayBeforeYesterday.add(Calendar.DATE, -2);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        Sales sales = mock(Sales.class);
        when(sales.getEffectiveFrom()).thenReturn(theDayBeforeYesterday.getTime());
        when(sales.getEffectiveTo()).thenReturn(yesterday.getTime());

        boolean isOutOfEffectiveDate = salesApp.isSalesOutOfEffectiveDate(sales);

        assertTrue(isOutOfEffectiveDate);
    }

    @Test
    public void testIsSalesOutOfEffectiveDate_givenEffectiveStartDateIsTomorrowAndEffectiveEndDateIsTheDayAfterTomorrow_thenReturnTrue() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        Calendar theDayAfterTomorrow = Calendar.getInstance();
        theDayAfterTomorrow.add(Calendar.DATE, 2);

        Sales sales = mock(Sales.class);
        when(sales.getEffectiveFrom()).thenReturn(tomorrow.getTime());
        when(sales.getEffectiveTo()).thenReturn(theDayAfterTomorrow.getTime());

        boolean isOutOfEffectiveDate = salesApp.isSalesOutOfEffectiveDate(sales);

        assertTrue(isOutOfEffectiveDate);
    }

    @Test
    public void testGetReportHeaders_givenIsNatTradeTrue_thenReturnCorrectHeadersContainsTime() {
        boolean isNatTrade = true;

        List<String> reportHeaders = salesApp.getReportHeaders(isNatTrade);

        assertEquals("Sales ID", reportHeaders.get(0));
        assertEquals("Sales Name", reportHeaders.get(1));
        assertEquals("Activity", reportHeaders.get(2));
        assertEquals("Time", reportHeaders.get(3));
    }

    @Test
    public void testGetReportHeaders_givenIsNatTradeFalse_thenReturnCorrectHeadersContainsLocalTime() {
        boolean isNatTrade = false;

        List<String> reportHeaders = salesApp.getReportHeaders(isNatTrade);

        assertEquals("Sales ID", reportHeaders.get(0));
        assertEquals("Sales Name", reportHeaders.get(1));
        assertEquals("Activity", reportHeaders.get(2));
        assertEquals("Local Time", reportHeaders.get(3));
    }

    @Test
    public void testUploadReportAsXml_givenSalesActivityReport_thenVerifyReportToXmlAndUploadDocumentUsage() {
        String fakeXmlString = "XML_STRING";
        SalesActivityReport salesActivityReport = mock(SalesActivityReport.class);
        when(salesActivityReport.toXml()).thenReturn(fakeXmlString);
        doNothing().when(ecmService).uploadDocument(any());

        salesApp.uploadReportAsXml(salesActivityReport);

        verify(salesActivityReport, times(1)).toXml();
        verify(ecmService, times(1)).uploadDocument(fakeXmlString);
    }

    @Test
    public void testIsSalesReportDataValid_givenSalesReportDataWithTypeIsNOTSalesActivity_thenReturnFalse() {
        String salesDataType = "NOT_ACTIVITY";

        SalesReportData notSalesActivityTypeSalesReportData = mock(SalesReportData.class);
        when(notSalesActivityTypeSalesReportData.getType()).thenReturn(salesDataType);

        boolean salesReportDataValid = salesApp.isSalesReportDataValid(true, notSalesActivityTypeSalesReportData);

        assertFalse(salesReportDataValid);
        verify(notSalesActivityTypeSalesReportData, times(0)).isConfidential();
        verify(notSalesActivityTypeSalesReportData, times(1)).getType();
    }

    @Test
    public void testIsSalesReportDataValid_givenSalesReportDataWithTypeIsSalesActivityAndIsConfidentialIsFalse_thenReturnTrue() {
        String salesDataType = "SalesActivity";

        SalesReportData salesActivitySalesReportData = mock(SalesReportData.class);
        when(salesActivitySalesReportData.getType()).thenReturn(salesDataType);
        when(salesActivitySalesReportData.isConfidential()).thenReturn(false);


        boolean salesReportDataValid = salesApp.isSalesReportDataValid(true, salesActivitySalesReportData);

        assertTrue(salesReportDataValid);
        verify(salesActivitySalesReportData, times(1)).isConfidential();
        verify(salesActivitySalesReportData, times(1)).getType();
    }

    @Test
    public void testIsSalesReportDataValid_givenIsSupervisorIsTrueSalesReportDataWithTypeIsSalesActivityAndIsConfidentialIsTrue_thenReturnTrue() {
        String salesDataType = "SalesActivity";

        boolean isSupervisor = true;
        SalesReportData salesActivitySalesReportData = mock(SalesReportData.class);
        when(salesActivitySalesReportData.getType()).thenReturn(salesDataType);
        when(salesActivitySalesReportData.isConfidential()).thenReturn(true);

        boolean salesReportDataValid = salesApp.isSalesReportDataValid(isSupervisor, salesActivitySalesReportData);

        assertTrue(salesReportDataValid);
        verify(salesActivitySalesReportData, times(1)).isConfidential();
        verify(salesActivitySalesReportData, times(1)).getType();
    }

    @Test
    public void testIsSalesReportDataValid_givenIsSupervisorIsFalseSalesReportDataWithTypeIsSalesActivityAndIsConfidentialIsTrue_thenReturnFalse() {
        String salesDataType = "SalesActivity";

        boolean isSupervisor = false;
        SalesReportData salesActivitySalesReportData = mock(SalesReportData.class);
        when(salesActivitySalesReportData.getType()).thenReturn(salesDataType);
        when(salesActivitySalesReportData.isConfidential()).thenReturn(true);

        boolean salesReportDataValid = salesApp.isSalesReportDataValid(isSupervisor, salesActivitySalesReportData);

        assertFalse(salesReportDataValid);
        verify(salesActivitySalesReportData, times(1)).isConfidential();
        verify(salesActivitySalesReportData, times(1)).getType();
    }

    @Test
    public void testGetFilteredReportDataList_givenFiveDataWithThreeValid_thenFilteredListContainsThreeRecord() {
        SalesApp spySalesApp = spy(new SalesApp());
        boolean isSupervisor = true;

        SalesReportData validSalesReportData = mock(SalesReportData.class);
        doReturn(true).when(spySalesApp).isSalesReportDataValid(isSupervisor, validSalesReportData);
        SalesReportData wrongSalesReportData = mock(SalesReportData.class);
        doReturn(false).when(spySalesApp).isSalesReportDataValid(isSupervisor, wrongSalesReportData);

        List<SalesReportData> filteredReportDataList = spySalesApp.getFilteredReportDataList(isSupervisor,
                Arrays.asList(
                        validSalesReportData,
                        validSalesReportData,
                        wrongSalesReportData,
                        validSalesReportData,
                        wrongSalesReportData)
        );

        assertEquals(3L, filteredReportDataList.size());
        assertEquals(validSalesReportData, filteredReportDataList.get(0));
        assertEquals(validSalesReportData, filteredReportDataList.get(1));
        assertEquals(validSalesReportData, filteredReportDataList.get(2));
        verify(spySalesApp, times(3)).isSalesReportDataValid(isSupervisor, validSalesReportData);
        verify(spySalesApp, times(2)).isSalesReportDataValid(isSupervisor, wrongSalesReportData);
    }

    @Test
    public void testGetLimitedSalesReportData_givenMaxRow1AndReportListWithSize2_thenReturnListWithSize1() {
        SalesReportData salesReportData = mock(SalesReportData.class);
        List<SalesReportData> salesReportDataListSize2 = Arrays.asList(salesReportData, salesReportData);
        int maxRow = 1;

        List<SalesReportData> limitedSalesReportData = salesApp.getLimitedSalesReportData(maxRow, salesReportDataListSize2);

        assertEquals(1, limitedSalesReportData.size());
    }

    @Test
    public void testGetLimitedSalesReportData_givenMaxRow2AndReportListWithSize1_thenReturnListWithSize2() {
        SalesReportData salesReportData = mock(SalesReportData.class);
        List<SalesReportData> salesReportDataListSize1 = Collections.singletonList(salesReportData);
        int maxRow = 2;

        List<SalesReportData> limitedSalesReportData = salesApp.getLimitedSalesReportData(maxRow, salesReportDataListSize1);

        assertEquals(1, limitedSalesReportData.size());
    }


    @Test
    public void testGetSalesBySalesId_givenASalesId_thenReturnASalesWithCorrectIdAndSalesDaoCalled() {
        String salesId = "S123";
        Sales mockSales = mock(Sales.class);
        when(salesDao.getSalesBySalesId(anyString())).thenReturn(mockSales);

        Sales sales = salesApp.getSalesBySalesId(salesId);

        assertEquals(mockSales, sales);
        verify(salesDao, times(1)).getSalesBySalesId(any());
    }

    @Test
    public void testGetSalesReportDataBySales_givenASales_thenReturnSalesReportDataAndSalesReportDaoCalled() {
        Sales mockSales = mock(Sales.class);
        List<SalesReportData> mockSalesReportDataList = Collections.singletonList(mock(SalesReportData.class));
        when(salesReportDao.getReportData(any())).thenReturn(mockSalesReportDataList);

        List<SalesReportData> reportData = salesApp.getSalesReportDataBySales(mockSales);
        assertEquals(mockSalesReportDataList, reportData);
        verify(salesReportDao, times(1)).getReportData(any());

    }

    @Test
    public void testGenerateSalesActivityReport_givenSalesIdWithNull_thenWillNotCallGetSalesBySalesId() {
        String salesId = null;
        int maxRow = 10;
        boolean isNatTrade = true;
        boolean isSupervisor = true;
        SalesApp spySalesApp = spy(new SalesApp());

        spySalesApp.generateSalesActivityReport(salesId, maxRow, isNatTrade, isSupervisor);

        verify(spySalesApp, times(1)).isSalesIdValid(any());
        verify(spySalesApp, times(0)).getSalesBySalesId(any());
    }

    @Test
    public void testGenerateSalesActivityReport_givenGetSalesByIdReturnSalesOutOfEffectiveDate_thenWillNotCallGetSalesReportDataBySales() {
        String salesId = "S01";
        int maxRow = 10;
        boolean isNatTrade = true;
        boolean isSupervisor = true;

        SalesApp spySalesApp = spy(new SalesApp());
        Sales sales = mock(Sales.class);
        doReturn(sales).when(spySalesApp).getSalesBySalesId(any());
        doReturn(true).when(spySalesApp).isSalesOutOfEffectiveDate(any());

        spySalesApp.generateSalesActivityReport(salesId, maxRow, isNatTrade, isSupervisor);

        verify(spySalesApp, times(1)).isSalesIdValid(any());
        verify(spySalesApp, times(1)).getSalesBySalesId(any());
        verify(spySalesApp, times(1)).isSalesOutOfEffectiveDate(any());
        verify(spySalesApp, times(0)).getSalesReportDataBySales(any());
    }

    @Test
    public void testGenerateSalesActivityReport_givenGetSalesByIdReturnSalesInEffectiveDate_thenWillNotCallGetSalesReportDataBySales() {
        String salesId = "S01";
        int maxRow = 10;
        boolean isNatTrade = true;
        boolean isSupervisor = true;

        SalesApp spySalesApp = spy(new SalesApp());

        Sales sales = mock(Sales.class);
        doReturn(sales).when(spySalesApp).getSalesBySalesId(any());

        doReturn(false).when(spySalesApp).isSalesOutOfEffectiveDate(any());

        doReturn(Collections.emptyList()).when(spySalesApp).getSalesReportDataBySales(any());

        doReturn(Collections.emptyList()).when(spySalesApp).getFilteredReportDataList(anyBoolean(), any());
        doReturn(Collections.emptyList()).when(spySalesApp).getLimitedSalesReportData(anyInt(), any());

        doReturn(Collections.emptyList()).when(spySalesApp).getReportHeaders(anyBoolean());
        SalesActivityReport salesActivityReport = mock(SalesActivityReport.class);
        doReturn(salesActivityReport).when(spySalesApp).generateReport(any(), any());

        spySalesApp.generateSalesActivityReport(salesId, maxRow, isNatTrade, isSupervisor);

        verify(spySalesApp, times(1)).isSalesIdValid(any());
        verify(spySalesApp, times(1)).getSalesBySalesId(any());
        verify(spySalesApp, times(1)).isSalesOutOfEffectiveDate(any());
        verify(spySalesApp, times(1)).getSalesReportDataBySales(any());
        verify(spySalesApp, times(1)).getFilteredReportDataList(anyBoolean(), any());
        verify(spySalesApp, times(1)).getLimitedSalesReportData(anyInt(), any());
        verify(spySalesApp, times(1)).getReportHeaders(anyBoolean());
        verify(spySalesApp, times(1)).generateReport(any(), any());
        verify(spySalesApp, times(1)).uploadReportAsXml(any());
    }

}
