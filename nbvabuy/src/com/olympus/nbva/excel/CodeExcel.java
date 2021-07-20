package com.olympus.nbva.excel;
 
import java.io.File;
import java.util.TreeMap;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import com.olympus.nbva.assets.AssetData;
import com.olympus.nbva.contracts.ContractData;
import com.olympus.nbvabuy.NbvaBuyout;
import com.olympus.olyutil.Olyutil;
import com.olympus.olyutil.log.OlyLog;

import java.util.logging.Handler;
import java.util.logging.Logger;


@WebServlet("/nbvabuyexcel")
public class CodeExcel extends HttpServlet {

	
	 static Logger logHandle = Logger.getLogger(CodeExcel.class.getCanonicalName());

	/***********************************************************************************************************************************/
	//   Map<String, CellStyle> styles = createStyles(workbook); // return styles to Hash
	// Ex. --> titleCell.setCellStyle(styles.get("title")); // deref title in hash and set cell
	public static Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap();
        
        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)18);
        titleFont.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(titleFont);
        style.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());  
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
        styles.put("title", style); // assign to Map
        
        Font monthFont = wb.createFont();
        monthFont.setFontHeightInPoints((short)11);
        monthFont.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("header", style); // assign to Map

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("cell", style); // assign to Map

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put("formula", style); // assign to Map

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put("formula_2", style); // assign to Map

        return styles;
    }
	
	/***********************************************************************************************************************************/
	public static XSSFSheet newWorkSheet(XSSFWorkbook workbook, String label) {

		XSSFSheet sheet = workbook.createSheet(label);
		return sheet;
	}
	/***********************************************************************************************************************************/
	public static XSSFWorkbook newWorkbook() {

		XSSFWorkbook workbook = new XSSFWorkbook();
		return workbook;
	}
	/***********************************************************************************************************************************/
	public static int getRandomNumber() {

		int min = 100000;
		int max = 999999;

		// Generate random int value from min to max
		// System.out.println("Random value in int from "+min+" to "+max+ ":");
		int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
		System.out.println("*** RndNum=" + random_int);

		return (random_int);
	}

	/****************************************************************************************************************************************************/
	public static HashMap<String, String> getChargeTypes() {
		HashMap<String, String> chargeTypeMap = new HashMap<String, String>();

		chargeTypeMap.put("1", "RENTAL");
		chargeTypeMap.put("0001", "RENTAL");
		chargeTypeMap.put("10", "LATE CHARGES");
		chargeTypeMap.put("0010", "LATE CHARGES");
		chargeTypeMap.put("M0012", "RENTAL");
		chargeTypeMap.put("M0041", "SERVICE");
		chargeTypeMap.put("M0056", "DOCUMENT FEE");
		chargeTypeMap.put("M0083", "PREMIUM PROTECTION");
		chargeTypeMap.put("M0042", "EndoTherapy Equip");
		chargeTypeMap.put("M0048", "SUPPLIES");
		chargeTypeMap.put("M0081", "CPO - 18 MO SERVICE");
		chargeTypeMap.put("M0058", "DEFAULT INTEREST");
		
		return(chargeTypeMap);
	}
	/****************************************************************************************************************************************************/
	public static void contractHeader(XSSFWorkbook workbook, XSSFSheet sheet, ArrayList<String> headerArr) {

		int rowNum = 4;
		int colNum = 0;
		
		
		Map<String, CellStyle> styles = createStyles(workbook);
		
		
		Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(45);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("NBVA Asset List");
        
        
        titleCell.setCellStyle(styles.get("cell"));
        
        titleCell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$K$1"));
       
        
        
		 Font font = workbook.createFont();
         font.setFontHeightInPoints((short) 12);
         font.setFontName("Times New Roman");
         font.setColor(IndexedColors.BLACK.getIndex());
         font.setBold(true);
         CellStyle style = workbook.createCellStyle();
         
         
         
         
         
         style.setFont(font);
         
         style.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());  
         style.setFillPattern(FillPatternType.SOLID_FOREGROUND);  
         style.setBorderRight(BorderStyle.THIN);
			style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderLeft(BorderStyle.THIN);
			style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderTop(BorderStyle.THIN);
			style.setTopBorderColor(IndexedColors.BLACK.getIndex());
	 
			 
			 
			
			
         
		for (Object field : headerArr) {
			Row row = sheet.createRow(rowNum++);
			Cell cell = row.createCell(colNum);
			if (field instanceof String) {
				cell.setCellStyle(style);
				cell.setCellValue((String) field);
			}
		}	
		sheet.autoSizeColumn(0); 
	}
	/****************************************************************************************************************************************************/
	public static void assetHeader(XSSFWorkbook workbook, XSSFSheet sheet, ArrayList<String> headerArr) {
			
		Row row = sheet.createRow(14);
		int colNum = 0;
		 Font font = workbook.createFont();
         font.setFontHeightInPoints((short) 12);
         font.setFontName("Times New Roman");
         font.setColor(IndexedColors.BLACK.getIndex());
         font.setBold(true);
         CellStyle style = workbook.createCellStyle();
         style.setFont(font);
         
         
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
	 
         
         style.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());  
         style.setFillPattern(FillPatternType.SOLID_FOREGROUND);  
		
		for (Object field : headerArr) {
			Cell cell = row.createCell(colNum++);
			if (field instanceof String) {
				cell.setCellStyle(style);
				cell.setCellValue((String) field);
			}
		}
	}
	
	/****************************************************************************************************************************************************/
	public static void loadWorkSheetContracts(XSSFWorkbook workbook, XSSFSheet sheet, List<Pair<ContractData, List<AssetData> >> rtnPair ) {
		int listArrSZ = rtnPair.size();
		 ContractData contractData = new ContractData();
		 
		 
		 		
		if (listArrSZ > 0) {	
			//System.out.println("*** listArrSZ=" + listArrSZ);
			for (int i = 0; i < listArrSZ; i++ ) {
				contractData = rtnPair.get(i).getLeft();
		 
				CellStyle style = workbook.createCellStyle();
				style.setBorderRight(BorderStyle.THIN);
				style.setRightBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderBottom(BorderStyle.THIN);
				style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderLeft(BorderStyle.THIN);
				style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderTop(BorderStyle.THIN);
				style.setTopBorderColor(IndexedColors.BLACK.getIndex());	
				
		            
				Row row = sheet.getRow(4);
				Cell cell = row.createCell(1);
				cell.setCellStyle(style);
				cell.setCellValue((String) contractData.getContractID());
				
				row = sheet.getRow(5); // AgreementNum
				cell = row.createCell(1);
				cell.setCellStyle(style);
				cell.setCellValue((String) contractData.getCustomerID());		
				//cell.setCellValue((String) "TBD");
				
				
				row = sheet.getRow(6);
				cell = row.createCell(1);
				cell.setCellStyle(style);
				cell.setCellValue((String) contractData.getCustomerName());		
		
				row = sheet.getRow(7);
				sheet.autoSizeColumn(1); 
				cell = row.createCell(1);
				cell.setCellStyle(style);
				cell.setCellValue((String) contractData.getCommenceDate());	
				
				row = sheet.getRow(8);
				cell = row.createCell(1);
				cell.setCellStyle(style);
				cell.setCellValue((String) contractData.getTermDate());
				
				
				row = sheet.getRow(9);
				cell = row.createCell(1);
				cell.setCellStyle(style);
				cell.setCellValue((long) contractData.getTerm());
				style.setAlignment(HorizontalAlignment.LEFT);
				
				row = sheet.getRow(10);
				cell = row.createCell(1);
				cell.setCellStyle(style);
				cell.setCellValue((double) contractData.getEquipRate());
				style.setAlignment(HorizontalAlignment.LEFT);
				
				row = sheet.getRow(11);
				cell = row.createCell(1);
				cell.setCellStyle(style);
				cell.setCellValue((double) contractData.getServiceRate());
				style.setAlignment(HorizontalAlignment.LEFT);
			}
		}
	}

	
	/****************************************************************************************************************************************************/
	public static void loadWorkSheetAssets(XSSFWorkbook workbook, XSSFSheet sheet, List<Pair<ContractData, List<AssetData> >> rtnPair ) {
		AssetData assetData = new AssetData();
		int listArrSZ = rtnPair.size();
		
		if (listArrSZ > 0) {	
			for (int i = 0; i < listArrSZ; i++ ) {
				int rtnArrSZ = rtnPair.get(i).getRight().size();
				List<AssetData> assetList = new ArrayList<AssetData>();
				assetList	= rtnPair.get(i).getRight();
				//System.out.println("<h5> listArrSZ =" + listArrSZ + " -- rtnArrSZ=" +  rtnArrSZ + "--</h5>");
				for (int n = 0; n < rtnArrSZ; n++ ) {
					AssetData asset = new AssetData();
					asset = assetList.get(n);
					//System.out.println("*** AssetReturn: EquipmentType=" + asset.getEquipType() + "--");
					//System.out.println("*** AssetReturn: N=" + n + " -- CustomerID=" + asset.getCustomerID() + "--");
					
					
					CellStyle style = workbook.createCellStyle();
					style.setBorderRight(BorderStyle.THIN);
					style.setRightBorderColor(IndexedColors.BLACK.getIndex());
					style.setBorderBottom(BorderStyle.THIN);
					style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					style.setBorderLeft(BorderStyle.THIN);
					style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					style.setBorderTop(BorderStyle.THIN);
					style.setTopBorderColor(IndexedColors.BLACK.getIndex());	
					
					
					
					Row row = sheet.createRow(n + 15);
					Cell cell = row.createCell(0);
					cell.setCellValue((long) asset.getAssetId());
					
					cell.setCellStyle(style);
					cell = row.createCell(1);
					cell.setCellValue((String) asset.getEquipType());
					//sheet.autoSizeColumn(1); 
					cell.setCellStyle(style);
					/*
					cell = row.createCell(2);
					cell.setCellStyle(style);
					cell.setCellValue((String) asset.getCustomerID());
					sheet.autoSizeColumn(2); 
					
					*/
					
					cell = row.createCell(2);
					cell.setCellStyle(style);
					cell.setCellValue((String) asset.getEquipDesc());					
					//sheet.autoSizeColumn(2); 
					cell = row.createCell(3);
					cell.setCellStyle(style);
					cell.setCellValue((String) asset.getModel());
					//sheet.autoSizeColumn(3); 
					cell = row.createCell(4);
					cell.setCellStyle(style);
					cell.setCellValue( asset.getSerNum().replaceAll("null", ""));
					//sheet.autoSizeColumn(4); 
					cell = row.createCell(5);
					cell.setCellStyle(style);
					cell.setCellValue((int) asset.getQty());
					//sheet.autoSizeColumn(5); 
					cell = row.createCell(6);
					cell.setCellStyle(style);
					cell.setCellValue((String) asset.getEquipAddr1());
					//sheet.autoSizeColumn(6); 
					cell = row.createCell(7);
					cell.setCellStyle(style);
					cell.setCellValue((String) asset.getEquipCity());
					//sheet.autoSizeColumn(7); 
					
					cell = row.createCell(8);
					cell.setCellStyle(style);
					cell.setCellValue((String) asset.getEquipState());
					//sheet.autoSizeColumn(8); 
					
					cell = row.createCell(9);
					cell.setCellStyle(style);
					cell.setCellValue((String) asset.getEquipZip());
					//sheet.autoSizeColumn(9); 
					
					//cell = row.createCell(10);
					//cell.setCellStyle(style);
					//cell.setCellValue((int) asset.getDispCode());
					//sheet.autoSizeColumn(10); 
					
					
				/*	
					cell = row.createCell(11);
					cell.setCellStyle(style);
					cell.setCellValue((double) asset.getResidAmt());
					sheet.autoSizeColumn(11); 
					
					cell = row.createCell(12);
					cell.setCellStyle(style);
					cell.setCellValue((double) asset.getEquipCost());
					sheet.autoSizeColumn(12); 
					
					cell = row.createCell(13);
					cell.setCellStyle(style);
					cell.setCellValue((double) asset.getaRentalAmt());
					sheet.autoSizeColumn(13); 
					cell = row.createCell(14);
					cell.setCellStyle(style);
					cell.setCellValue((int) asset.getDispCode());
					sheet.autoSizeColumn(14);
					
					
				*/
				} // end for n
				//System.out.println("*** End n loop");
			} //end for i
			//System.out.println("*** End i loop");
		} // end if	
		//System.out.println("*** End if");
	}
	/****************************************************************************************************************************************************/
	public static void doBuyoutInvoice(XSSFWorkbook workbook, String tab, List<Pair<ContractData, List<AssetData> >> rtnPair, String dateStamp  ) throws IOException {

	
		int listArrSZ = rtnPair.size();
		 ContractData contractData = new ContractData();
		 XSSFSheet sheet1 = workbook.getSheet(tab);
		// Sheet mySheet = wb.getSheetAt(0);
		String contractID = "";
		String agreementNum = "";
		String custName = "";
		String custAddr1 = "";
		String custAddr2 = "";
		String custCity = "";
		String custState = "";
		String custZip = "";
		String boDate = "";
		String buyOutAmt = "";
		String effDate = "";
		double buy = 0.00;
		String dFmt = Olyutil.formatDate(dateStamp, "yyyy-MM-dd", "MMMM d, yyyy");
		String dateToday = Olyutil.formatDate(dateStamp, "yyyy-MM-dd", "yyyyMMdd");
		String invoiceNum = "";
		double buyOutWithTax = 0.00;
		
		if (listArrSZ > 0) {	
			//System.out.println("*** listArrSZ=" + listArrSZ);
		 
			 
			
			for (int i = 0; i < listArrSZ; i++ ) {
				contractData = rtnPair.get(i).getLeft();
				agreementNum = contractData.getCustomerID();
				effDate = contractData.getEffectiveDate();
				custName = contractData.getCustomerName();
				custAddr1 = contractData.getCustomerAddr1();
				custAddr2 = contractData.getCustomerAddr2();
				custCity = contractData.getCustomerCity();
				custState = contractData.getCustomerState();
				custZip = contractData.getCustomerZip();
				buy = contractData.getBuyTotal();	
				buyOutWithTax = contractData.getBuyOutWithTax();
			}
			invoiceNum = agreementNum + "-" + dateToday +  "-" + getRandomNumber();
			
			
			//logHandle.info(dateStamp + ": " + "-- UserID:" +  userID   +  "-- Processing ID: " + idVal +    "--");
			logHandle.info(dateStamp + ": " + "-- invoiceNum:" +  invoiceNum  );
			//System.out.println("** invNum=" + invoiceNum + "--");
			//String dFmt2 = Olyutil.formatDate(effDate, "yyyy-MM-dd", "MMMM d, yyyy");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			 LocalDate effectiveDate = LocalDate.parse(effDate, formatter);
			LocalDate effDateMinus1 = effectiveDate.plusDays(-1);
			//String dFmtPlus = Olyutil.formatDate(effDateMinus1.toString(), "yyyy-MM-dd", "MMMM d, yyyy");
			
			
			String dFmtPlus = Olyutil.formatDate(effDateMinus1.toString(), "yyyy-MM-dd", "M/d/yyyy");
			String dFmt2 = Olyutil.formatDate(effDate, "yyyy-MM-dd", "M/d/yyyy");
			 
			XSSFRow row = sheet1.getRow(5);
			XSSFCell cell = row.getCell(4);
			cell.setCellValue(dFmt); 
			
			// add invoice number -- JB 2021-01-26
		 
			row = sheet1.getRow(4);
			cell = row.getCell(4);
			cell.setCellValue(invoiceNum);
			/* 
			row = sheet1.getRow(4);
			cell = row.getCell(4);
			cell.setCellValue(contractData.getInvoice());
			*/
			
			row = sheet1.getRow(10);
			cell = row.getCell(1);
			cell.setCellValue(custName);
		
			
			row = sheet1.getRow(11);
			cell = row.getCell(1);
			cell.setCellValue(custAddr1);
			
			if (! Olyutil.isNullStr(custAddr2)) {
				row = sheet1.getRow(12);
				cell = row.getCell(1);
				cell.setCellValue(custAddr2);
				row = sheet1.getRow(13);
				cell = row.getCell(1);
				cell.setCellValue(custCity + ", " + custState+ " " + custZip);	
				
			} else {
				row = sheet1.getRow(12);
				cell = row.getCell(1);
				cell.setCellValue(custCity + ", " + custState+ " " + custZip);	
			}
			
			// Fix date 2021=01-26
			row = sheet1.getRow(16);
			cell = row.getCell(1);
			//cell.setCellValue(effDateMinus1.toString());
			cell.setCellValue(dFmtPlus);
			
			
			
			
			row = sheet1.getRow(16);
			cell = row.getCell(4);
			cell.setCellValue("FIS"+agreementNum);
			
			
			
			row = sheet1.getRow(21);
			cell = row.getCell(4);
			//cell.setCellValue(Olyutil.decimalfmt(contractData.getBuyOut(), "$###,##0.00"));
			//cell.setCellValue(Olyutil.decimalfmt(contractData.getBuyOutWithTax(), "$###,##0.00"));
			
			cell.setCellValue(contractData.getBuyOut()); // make value double
			
			double taxedBuyout_t = contractData.getBuyOutWithTax();
			double buyOut = contractData.getBuyOut();
			double taxesPaid_t =  taxedBuyout_t - buyOut;
			
			// Set tax payment in invoice
			row = sheet1.getRow(37); // Set tax
			cell = row.getCell(4);
			//cell.setCellValue(Olyutil.decimalfmt(contractData.getBuyOutWithTax(), "$###,##0.00"));
			
			cell.setCellValue(taxesPaid_t); // make double
			
			
			

			row = sheet1.getRow(40);
			cell = row.getCell(4);
			//cell.setCellValue(Olyutil.decimalfmt(contractData.getBuyOutWithTax(), "$###,##0.00"));
			
			cell.setCellValue(contractData.getBuyOutWithTax()); // make double

			
			//cell.setCellValue(Olyutil.decimalfmt(contractData.getBuyOut(), "$###,##0.00"));
			//sheet1.addMergedRegion(new CellRangeAddress(40, 41, 4, 4));
		}
		
	}
	
	
	/****************************************************************************************************************************************************/

	public static void doBuyoutLetter(XSSFWorkbook wb, String excelTemplateNew, String tab,  String templateFile,   String dateStamp, List<Pair<ContractData, List<AssetData> >> rtnPair ) throws IOException {
		int listArrSZ = rtnPair.size();
		 ContractData contractData = new ContractData();



		FileOutputStream fileOut = new FileOutputStream(excelTemplateNew);
		XSSFSheet sheet1 = wb.getSheet(tab);
		// Sheet mySheet = wb.getSheetAt(0);
		String contractID = "";
		String agreementNum = "";
		String custName = "";
		String custAddr1 = "";
		String custAddr2 = "";
		String custCity = "";
		String custState = "";
		String custZip = "";
		String boDate = "";
		String buyOutAmt = "";
		String effectiveDate = "";
		String dFmt = Olyutil.formatDate(dateStamp, "yyyy-MM-dd", "MMMM d, yyyy");
		
		XSSFRow row = sheet1.getRow(9);
		XSSFCell cell = row.getCell(5);
		cell.setCellValue(dFmt); 
		
		 

		if (listArrSZ > 0) {

			//System.out.println("*** listArrSZ=" + listArrSZ);
			 for (int i = 0; i < listArrSZ; i++) {
				contractData = rtnPair.get(i).getLeft();
				contractID = contractData.getContractID();
				agreementNum = contractData.getCustomerID();
				custName = contractData.getCustomerName();
				custAddr1 = contractData.getCustomerAddr1();
				custAddr2 = contractData.getCustomerAddr2();
				custCity = contractData.getCustomerCity();
				custState = contractData.getCustomerState();
				custZip = contractData.getCustomerZip();
				 
				boDate = contractData.getBuyOutDate();
				//buyOutAmt = Olyutil.decimalfmt(contractData.getBuyOut(), "$###,##0.00");
				
				//buyOutAmt = Olyutil.decimalfmt(contractData.getBuyOutWithTax(), "$###,##0.00");
				buyOutAmt = Olyutil.decimalfmt(contractData.getBuyOutInvoiceTotal(), "$###,##0.00");
				
				
				//System.out.println("*** contractID=" + contractID + "-- AgreementNum=" + agreementNum);
				effectiveDate = contractData.getEffectiveDate();
				//System.out.println("*** effectiveDate = " + effectiveDate + "--");
			}  
		}
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		 //DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		 
		 DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
		 
	     
		//convert String to LocalDate
	     LocalDate effDate = LocalDate.parse(effectiveDate, formatter);
	     LocalDate effDateMinus15 = effDate.plusDays(-15);
	     LocalDate effDateMinus1 = effDate.plusDays(-1);
	     
	     String dMinus1 = formatter2.format(effDateMinus1);
	     String dMinus15 = formatter2.format(effDateMinus15);
	     
	     
		//String dFmt2 = Olyutil.formatDate(boDate, "yyyy-MM-dd", "MMMM dd, yyyy");
		 String line1 = "this purchase.   Failure to fax the countersigned letter by "   + dMinus15 +  " and remit payment no later";
		 
		// String line1 = "this purchase.   Failure to fax the countersigned letter by "   + dMinus1 +  " and remit payment no later";

		 
		 String line2 = " than " + dMinus1 + " shall be deemed a withdrawal of your intention to purchase the Equipment, and invoices";
		 
		row = sheet1.getRow(11);
		cell = row.getCell(5);
		cell.setCellValue(custName);
		
		row = sheet1.getRow(50);
		cell = row.getCell(8);
		cell.setCellValue(custName);
		
		row = sheet1.getRow(12);
		cell = row.getCell(5);
		cell.setCellValue(custAddr1);
		
		if (! Olyutil.isNullStr(custAddr2)) {
			row = sheet1.getRow(13);
			cell = row.getCell(5);
			cell.setCellValue(custAddr2);
			row = sheet1.getRow(14);
			cell = row.getCell(5);
			cell.setCellValue(custCity + ", " + custState+ " " + custZip);	
			
		} else {
			row = sheet1.getRow(13);
			cell = row.getCell(5);
			cell.setCellValue(custCity + ", " + custState+ " " + custZip);	
		}
		/*row = sheet1.getRow(14);
		cell = row.getCell(5);
		cell.setCellValue(custState+ " " + custZip);*/	

		
		
		row = sheet1.getRow(16);
		cell = row.getCell(5);
		cell.setCellValue("FIS"+agreementNum);

		row = sheet1.getRow(18);
		cell = row.getCell(5);
		cell.setCellValue(contractID);

		row = sheet1.getRow(22);
		cell = row.getCell(5);
		
		cell.setCellValue(buyOutAmt);

		row = sheet1.getRow(40);
		cell = row.getCell(1);
		cell.setCellValue(line1);
		
		row = sheet1.getRow(41);
		cell = row.getCell(1);
		cell.setCellValue(line2);
		
		wb.write(fileOut);
		// log.info("Written xls file");
		//fileOut.close();
		
		
	}
	/****************************************************************************************************************************************************/
	public static HashMap<String, String> getAssetMap(String id, ArrayList<String> strArr, String sep, HashMap<String, String> ctMap) {
		HashMap<String, String> assetMap = new HashMap<String, String>();
		String aDate = "";
		String contractID = "";
		String aID = "";
		String desc = "";
		String charges = "";
		String chargeType = "";
		for (String s : strArr) {	
			String[] items = s.split(sep);	
			//System.out.println("*** SZ=" + items.length + "-- " + s );
			contractID = items[0].trim(); 
			// Process ageFile
			if (contractID.equals(id)) {
				 //System.out.println("*** Match: " +  s );
				chargeType = items[2].trim();
				aDate = items[5].trim();
				aID = items[6].trim();
				desc = items[3].trim();
				charges = items[4].trim();
			 //System.out.println("**^^** Match: aID=" +  aID + "-- aDate=" + aDate + "-- CT=" + chargeType + "--");
				
				if (ctMap.containsKey(chargeType)) {
					System.out.println("*** Match CT -- AID:" + aID +  "-- Date="  +  aDate   +  "-- CT=" + chargeType + "-- Desc=" + ctMap.get(chargeType) + "--");
					assetMap.put(aID, aDate + "^" + ctMap.get(chargeType) + "^" + charges );
				} else {
					assetMap.put(aID, aDate);
				}
				
				
				//if (desc.equals("LATE CHARGES")) {
					//assetMap.put(aID, aDate + "^" + desc + "^" + charges );
				//} 
				
				
				
				
				
				
			}
			 
		 }
		//displayDataMapStr( assetMap, "getAssetMap:From dailyAge file");
		return(assetMap);
		
	}
	
	/****************************************************************************************************************************************************/
	
	// Buyout Statement
	public static void doInvoiceStatement(XSSFWorkbook workbook, String tab, List<Pair<ContractData, 
			List<AssetData> >> rtnPair, String dateStamp, ArrayList<String> ageArr, HashMap<String, String> invDateMapDB) throws IOException {

		String assetDate = "";
		HashMap<String, String> assetMap = new HashMap<String, String>();
		HashMap<String, String> chargeTypeMap = new HashMap<String, String>();
		int listArrSZ = rtnPair.size();
		ContractData contractData = new ContractData();
		AssetData assets = new AssetData();
		XSSFSheet sheet1 = workbook.getSheet(tab);
		// Sheet mySheet = wb.getSheetAt(0);
		String contractID = "";
		String agreementNum = "";
		String custName = "";
		String custAddr1 = "";
		String custAddr2 = "";
		String custCity = "";
		String custState = "";
		String custZip = "";
		String boDate = "";
		String buyOutAmt = "";
		String effDate = "";
		double buy = 0.00;
		double invoiceTot = 0.00;
		 String dFmt = Olyutil.formatDate(dateStamp, "yyyy-MM-dd", "MMMM d, yyyy");
		 String effDate_LC = "";
		 String desc_LC = "";
		 String charge_LV = "";
		 
		String[] lineArr = null;
		//System.out.println("** DATE=" + dFmt + "--");
		
		double contractTotal = 0.00;
		
		if (listArrSZ > 0) {	
			//System.out.println("*** listArrSZ=" + listArrSZ);
			chargeTypeMap = getChargeTypes();
			
			for (int i = 0; i < listArrSZ; i++ ) {
				contractData = rtnPair.get(i).getLeft();
				contractID = contractData.getContractID();
				agreementNum = contractData.getCustomerID();
				effDate = contractData.getEffectiveDate();
				custName = contractData.getCustomerName();
				custAddr1 = contractData.getCustomerAddr1();
				custAddr2 = contractData.getCustomerAddr2();
				custCity = contractData.getCustomerCity();
				custState = contractData.getCustomerState();
				custZip = contractData.getCustomerZip();
				buy = contractData.getBuyTotal();			
			}
			// Fix invoice date -- 2021-02-17
			 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			//assetMap = getAssetMap(contractID, ageArr, ";", chargeTypeMap);
			 
			// System.out.println("** ContractID=" + contractID  + "--");
			LocalDate effectiveDate = LocalDate.parse(effDate, formatter);
			LocalDate effDateMinus1 = effectiveDate.plusDays(-1);
			
			String dateToday = Olyutil.formatDate(dateStamp, "yyyy-MM-dd", "yyyyMMdd");
			
			
			String invoiceNum = agreementNum + "-" + dateToday;
			//System.out.println("** invNum=" + invoiceNum + "--");		 
			buyOutAmt = Olyutil.decimalfmt(contractData.getBuyOutWithTax(), "$###,##0.00");
			//displayDataMapStr( invDateMapDB, "From database");
			
			 //displayDataMapStr( assetMap, "From dailyAge file");

			//String buyOutAmt_noTax = Olyutil.decimalfmt(contractData.getBuyOut(), "$###,##0.00");
			
			double buyOutAmt_noTax =  contractData.getBuyOut() ;
			
			double buyOutAmt_withTax =  contractData.getBuyOutWithTax() ;
			
			// dFmt2 = Olyutil.formatDate(effDate, "yyyy-MM-dd", "MMMM dd, yyyy");
			String dFmt2 = Olyutil.formatDate(effDate, "yyyy-MM-dd", "M/d/yyyy");
			
			 
			double taxedAmt_t = contractData.getBuyOutWithTax() - contractData.getBuyOut();
			//String 	taxedAmt = Olyutil.decimalfmt(taxedAmt_t, "$###,##0.00");
			
			XSSFRow row = sheet1.getRow(3);
			XSSFCell cell = row.getCell(4);
			cell.setCellValue(dFmt); 
		
			 
			row = sheet1.getRow(18);
			cell = row.getCell(1);
			// set new invoice number
			cell.setCellValue(invoiceNum);
			 
			row = sheet1.getRow(18);
			cell = row.getCell(2);
			
			
			
			String dFmt3 = Olyutil.formatDate(effDateMinus1.toString(), "yyyy-MM-dd", "M/d/yyyy");
			
			
			//System.out.println("*** DATE=" + effDateMinus1.toString() + "-- dfmt3=" + dFmt3 + "--");
			
			
			cell.setCellValue(dFmt3);
			
			
			row = sheet1.getRow(18);
			cell = row.getCell(3);
			cell.setCellValue("Buyout Payment");
			
			row = sheet1.getRow(18);
			cell = row.getCell(4);
			
			
			//cell.setCellValue(buyOutAmt_noTax); // without tax
			
			cell.setCellValue(buyOutAmt_withTax); // with tax
			
			
			row = sheet1.getRow(8);
			cell = row.getCell(1);
			cell.setCellValue(custName);
		
			
			row = sheet1.getRow(9);
			cell = row.getCell(1);
			cell.setCellValue(custAddr1);
			
			if (! Olyutil.isNullStr(custAddr2)) {
				row = sheet1.getRow(10);
				cell = row.getCell(1);
				cell.setCellValue(custAddr2);
				row = sheet1.getRow(11);
				cell = row.getCell(1);
				cell.setCellValue(custCity + ", " + custState+ " " + custZip);	
				
			} else {
				row = sheet1.getRow(10);
				cell = row.getCell(1);
				cell.setCellValue(custCity + ", " + custState+ " " + custZip);	
			}
			
	
			
			row = sheet1.getRow(11);
			cell = row.getCell(4);
			cell.setCellValue("FIS"+agreementNum);
			
			/*row = sheet1.getRow(21);
			cell = row.getCell(4);
			cell.setCellValue(Olyutil.decimalfmt(contractData.getBuyOut(), "$###,##0.00"));
			*/

			
			
			//cell.setCellValue(Olyutil.decimalfmt(contractData.getBuyOut(), "$###,##0.00"));
			
			
			//sheet1.addMergedRegion(new CellRangeAddress(40, 41, 4, 4));
			int k = 19;
			int zz = 0;
			double amt = 0.00;
			double amt_LC = 0.00;
			
			// Process invoice hash
			
			
			
			 //displayDataMapStr(assetMap);
		  
			
			double invoicePayment = contractData.getPaymentWtax();
			//
			//System.out.println("***invPymt=" + invoicePayment + "--");
			/*************************************************************************************************************/
			// display invoice numbers and totals
			// Read and process AgeFile
			HashMap<String, String> tbdMap = new HashMap<String, String>();
			tbdMap.clear();
			String aDate = "";
			String contractNum = "";
			String aID = "";
			String desc = "";
			String charges = "";
			String chargeType = "";
			boolean effDateFound = false;
			for (String s : ageArr) {
				String[] items = s.split(";");	

				if (s.matches(".*\\b" + contractID  + "\\b.*") ) {
					//System.out.println("****!!!**** s=" + s);
					
					chargeType = items[2].trim();
					charges = items[4].trim();
					aDate = items[5].trim();
					aID = items[6].trim();
					desc = items[3].trim();
					desc = desc.replaceAll(" ", "_");
					tbdMap.put(desc, charges);
					if (aDate.equals(effDate)) {
						effDateFound = true;
					}
					
					
					row = sheet1.getRow(k++);
					cell = row.getCell(1);
					cell.setCellValue(aID);
					//String dFmt4 = Olyutil.formatDate(effDateMinus1.toString(), "yyyy-MM-dd", "M/d/yyyy");
					//String dFmt4 = Olyutil.formatDate(assetDate, "yyyy-MM-dd", "M/d/yyyy");
					String dFmt4 = Olyutil.formatDate(aDate, "yyyy-MM-dd", "M/d/yyyy");
					cell = row.getCell(2);
					cell.setCellValue( dFmt4);
					

					cell = row.getCell(3);
					//cell.setCellValue("Usage:");
					cell.setCellValue(desc);

					cell = row.getCell(4);
					
					
					cell.setCellValue(Olyutil.decimalfmt(Olyutil.strToDouble(charges), "$###,##0.00"));
					cell.setCellValue(Olyutil.strToDouble(charges));
					
					// System.out.println("**B** IVT=" + invoiceTot + "-- IP=" + invoicePayment +
					// "--");
					//invoiceTot += invoicePayment;
					invoiceTot += Olyutil.strToDouble(charges);
					
					
					
					
					
					
					
					
					
					
					
				} // end outer if
			} // end for
			
			//System.out.println("****!!!**** effDateChk=" + effDateFound);
			if (! effDateFound) { // add in 30 day TBD charges
				
				 //displayDataMapStr( tbdMap, "TBD Data");
				
				 for (Map.Entry<String, String> entry : tbdMap.entrySet()) {
						//System.out.println("*** Key:" + entry.getKey() + " --> Value:" + entry.getValue() + "--");
						// 2021-06-24 -- Fix TBD late charge
						if (entry.getKey().equals("LATE_CHARGES") ) {
							continue;
						}
						charges = entry.getValue();
						row = sheet1.getRow(k++);
						cell = row.getCell(1);
						cell.setCellValue("TBD");
						//String dFmt4 = Olyutil.formatDate(effDateMinus1.toString(), "yyyy-MM-dd", "M/d/yyyy");
						//String dFmt4 = Olyutil.formatDate(assetDate, "yyyy-MM-dd", "M/d/yyyy");
						String dFmt4 = Olyutil.formatDate(effDate, "yyyy-MM-dd", "M/d/yyyy");
						cell = row.getCell(2);
						cell.setCellValue( dFmt4);
						

						cell = row.getCell(3);
						//cell.setCellValue("Usage:");
						cell.setCellValue(entry.getKey());

						cell = row.getCell(4);
						
						
						cell.setCellValue(Olyutil.decimalfmt(Olyutil.strToDouble(charges), "$###,##0.00"));
						cell.setCellValue(Olyutil.strToDouble(charges));
						
						// System.out.println("**B** IVT=" + invoiceTot + "-- IP=" + invoicePayment +
						// "--");
						//invoiceTot += invoicePayment;
						invoiceTot += Olyutil.strToDouble(charges);
						
						
						
						
						
						
						
					}
			}
			
			
			
			
			
			
			
			/*************************************************************************************************************/
	
			
			String key = "";
			String val = "";
			String[] arrOfStr = null;
			  
			if (! assetMap.containsValue(effDate)) {
           	 //System.out.println("**** effDate not found:" + effDate + "--");
           	 assetMap.put("TBD", effDate);
            }
			Map<String, String> mapSort = new TreeMap<String, String>(assetMap); 
			Set set2 = mapSort.entrySet();
	         Iterator iterator2 = set2.iterator();
	        // while(iterator2.hasNext()) {
	              //Map.Entry mp = (Map.Entry)iterator2.next();
	             // key = mp.getKey().toString();
	             // val = mp.getValue().toString();
	              //System.out.print("**** mapSorted:" + mp.getKey() + ": ");
	              //System.out.println(mp.getValue() + "--");
	              
	               //System.out.println("**** mapSorted=" + key + "-- Value=" + val + "--");
	              
	              
	              
	              
	              
/*
	              row = sheet1.getRow(k);
					cell = row.getCell(1);
					cell.setCellValue(key);
					//String dFmt4 = Olyutil.formatDate(effDateMinus1.toString(), "yyyy-MM-dd", "M/d/yyyy");
					//String dFmt4 = Olyutil.formatDate(assetDate, "yyyy-MM-dd", "M/d/yyyy");
					String dFmt4 = Olyutil.formatDate(val, "yyyy-MM-dd", "M/d/yyyy");
					cell = row.getCell(2);
					cell.setCellValue( dFmt4);
			*/		
					
	          /*    
				if (val.matches(".*\\bLATE CHARGES\\b.*")  || val.matches(".*\\bSERVICE\\b.*")) {
					//System.out.println("*** Match LC=" + val + "--");
					arrOfStr = val.split("\\^");
					effDate_LC = arrOfStr[0];
					desc_LC = arrOfStr[1];
					charge_LV = arrOfStr[2];
					 row = sheet1.getRow(k);
						cell = row.getCell(1);
						cell.setCellValue(key);
						amt_LC  += Olyutil.strToDouble(charge_LV);
						String dFmt4 = Olyutil.formatDate(effDate_LC, "yyyy-MM-dd", "M/d/yyyy");
						cell = row.getCell(2);
						cell.setCellValue( dFmt4);
					
						//System.out.println("*** Match key=" + key + "-- Date=" + effDate_LC + "-- Charge=" + charge_LV + "--");
					
					
					cell = row.getCell(3);
					cell.setCellValue(desc_LC + ":");

					cell = row.getCell(4);
					cell.setCellValue(Olyutil.decimalfmt(Olyutil.strToDouble(charge_LV), "$###,##0.00"));
					 cell.setCellValue( Olyutil.strToDouble(charge_LV));
					

				} else {
					//System.out.println("*** NO Match LC=" + val + "--");
					
					row = sheet1.getRow(k);
					cell = row.getCell(1);
					cell.setCellValue(key);
					//String dFmt4 = Olyutil.formatDate(effDateMinus1.toString(), "yyyy-MM-dd", "M/d/yyyy");
					//String dFmt4 = Olyutil.formatDate(assetDate, "yyyy-MM-dd", "M/d/yyyy");
					String dFmt4 = Olyutil.formatDate(val, "yyyy-MM-dd", "M/d/yyyy");
					cell = row.getCell(2);
					cell.setCellValue( dFmt4);
					

					cell = row.getCell(3);
					cell.setCellValue("Usage:");

					cell = row.getCell(4);
					cell.setCellValue(Olyutil.decimalfmt(invoicePayment, "$###,##0.00"));
					cell.setCellValue(invoicePayment);
					// System.out.println("**B** IVT=" + invoiceTot + "-- IP=" + invoicePayment +
					// "--");
					invoiceTot += invoicePayment;
					// System.out.println("**A** IVT=" + invoiceTot + "-- IP=" + invoicePayment +
					// "--");

					// System.out.println("*** Setting (4) Key:" + entry.getKey() + " --> Value:" +
					// entry.getValue() + "-- k=" + k +"-- IT=" + invoiceTot + "--");

				}
				*/
					k++;		   
	              
	        // } // end while
			// check for  TBD payments -- no invoice in dailyAge file
            // System.out.println("**** Latest Invoice Date=" + contractData.getFinalInvDueDate() + "-- effDate=" + effDate + "--");
             
	         
			
			/***************************************************************************************************************/
			
			/*
			for (Map.Entry<String, Double> entry : invoiceDatesMap.entrySet()) {
				System.out.println("*** Key:" + entry.getKey() + " --> Value:" + entry.getValue() + "--");
				
				if (entry.getKey().equals("contractTotal")) {
					contractTotal = entry.getValue();
					k++;
					continue;
					
				}
				if (assetMap.containsKey(entry.getKey())) {
					assetDate = assetMap.get(entry.getKey());
					System.out.println("*** assetDate=" + assetDate   + "--");
				}
				
				row = sheet1.getRow(k);
				cell = row.getCell(1);
				cell.setCellValue(entry.getKey());
				//String dFmt4 = Olyutil.formatDate(effDateMinus1.toString(), "yyyy-MM-dd", "M/d/yyyy");
				String dFmt4 = Olyutil.formatDate(assetDate, "yyyy-MM-dd", "M/d/yyyy");
				cell = row.getCell(2);
				cell.setCellValue( dFmt4);
				
				cell = row.getCell(3);
				cell.setCellValue( "Usage:");
				
				cell = row.getCell(4);
				//cell.setCellValue( Olyutil.decimalfmt(entry.getValue(), "$###,##0.00"));	
				cell.setCellValue(entry.getValue());
				invoiceTot += entry.getValue();
				//System.out.println("*** Setting (4) Key:" + entry.getKey() + " --> Value:" + entry.getValue() + "-- k=" + k +"-- IT=" + invoiceTot + "--");
				k++;		
				
				
				
				
			} // End For process hash map
			
			
			*/
			
			/*   hold off on this section for now 2021-05-04 
			if (taxedAmt_t > 0.00) {
				row = sheet1.getRow(k);
				//System.out.println("*** Tax=" +  taxedAmt_t + "-- Row="  + k + "--");
				cell = row.getCell(2);
				cell.setCellValue( dFmt3);
				
				
				
				
				cell = row.getCell(3);
				cell.setCellValue("Tax Payment");
			
				cell = row.getCell(4);
				//System.out.println("*** Set  Tax=" +  taxedAmt_t + "-- Row="  + k + "-- cellVal=" + cell.getColumnIndex() + "--");
				cell.setCellValue(taxedAmt_t);
				
			}
			*/
			/***************************************************************************************************************************************/
	         // Write out total
			row = sheet1.getRow(43);
			cell = row.getCell(4);
			
			//System.out.println("*** BO=" + buyOutAmt + "--");
			
		    buyOutAmt = buyOutAmt.replace("$", "");
		    
		    
		    
			//double tot = Olyutil.strToDouble(buyOutAmt) + contractTotal;
		    double tot = Olyutil.strToDouble(buyOutAmt) + invoiceTot + amt_LC;
			//cell.setCellValue(Olyutil.decimalfmt((tot), "$###,##0.00"));
			 //System.out.println("*** Tot=" +  tot + "--BO=" + buyOutAmt + "-- IVT=" + invoiceTot + "--");
			
			
			 cell.setCellValue(tot); // remove taxes
			 contractData.setBuyOutInvoiceTotal(tot);
			
	 
			/***************************************************************************************************************************************/
	
			
			
		} // end if SZ	


	}
	/****************************************************************************************************************************************************************/
	public static void displayDataMapStr(Map<String, String> map, String tag) {

		for (Map.Entry<String, String> entry : map.entrySet()) {
			System.out.println("*** "  +  tag + "-- Key:" + entry.getKey() + " --> Value:" + entry.getValue() + "--");
		}
		System.out.println("*****************************************************************************************************");

	}
	
	/****************************************************************************************************************************************************************/

	public static void displayDataMapSD(Map<String, Double> map) {

		for (Map.Entry<String, Double> entry : map.entrySet()) {
			System.out.println("*** Key:" + entry.getKey() + " --> Value:" + entry.getValue() + "--");
		}
		System.out.println("*****************************************************************************************************");

	}
	/****************************************************************************************************************************************************/
	

	
	/****************************************************************************************************************************************************/

	
	// Service method
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
			HttpSession session = req.getSession();
			Date date = Olyutil.getCurrentDate();
			String dateStamp = date.toString();
			ArrayList<String> assetHeaderArr = new ArrayList<String>();
			ArrayList<String> ageArr = new ArrayList<String>();
			String contractHeaderFile = "C:\\Java_Dev\\props\\headers\\NBVA\\NBVA_ContractHrd.txt";
			String headerFile = "C:\\Java_Dev\\props\\headers\\NBVA\\NBVA_AssetHrdExcel.txt";
			String ageFile = "C:\\Java_Dev\\props\\nbvabuy\\dailyAge.csv";
			String FILE_NAME = "NBVA_Asset_List_Report_" + dateStamp + ".xlsx";
			String excelTemplate = "C:\\Java_Dev\\props\\nbvabuy\\excelTemplates\\letterUpdate.xlsx";		
			XSSFWorkbook workbook = null;
			XSSFSheet sheet = null;
			
			HashMap<String, String> invDateMapDB = (HashMap<String, String>) session.getAttribute("invDateMapDB");
			String logFileName = "nbvabuy.log";
			String directoryName = "D:/javalogs/logfiles/nbvabuy";
			Handler fileHandler =  OlyLog.setAppendLog(directoryName, logFileName, logHandle );
			
			//displayDataMapStr( invDateMapDB, "From database");
		assetHeaderArr = Olyutil.readInputFile(headerFile);

		String tab1 = "Buyout_Letter";
		workbook = new XSSFWorkbook(new FileInputStream(excelTemplate));
		FileOutputStream fileOut = new FileOutputStream(FILE_NAME);
		// String excelTemplateNew = "NBVA_BuyOut_Letter_" + dateStamp + ".xlsx";

		ageArr = Olyutil.readInputFile(ageFile);
		//Olyutil.printStrArray(ageArr);

		ArrayList<String> contractHeaderArr = new ArrayList<String>();
		contractHeaderArr = Olyutil.readInputFile(contractHeaderFile);
		List<Pair<ContractData, List<AssetData>>> list = (List<Pair<ContractData, List<AssetData>>>) session
				.getAttribute("rtnPair");

		// strArr = (ArrayList<String>) session.getAttribute("strArr");
		

		// XSSFSheet sheet2 = getWorkSheet(workbook, "Buyout_Invoice");
		doBuyoutInvoice(workbook, "Buyout_Invoice", list, dateStamp);
		doInvoiceStatement(workbook, "Buyout_Statement", list, dateStamp, ageArr, invDateMapDB);
		//System.out.println("** Call contractHeader");
		// workbook = newWorkbook();
		
		doBuyoutLetter(workbook, FILE_NAME, tab1, excelTemplate, dateStamp, list);
		sheet = newWorkSheet(workbook, "Asset List");
		contractHeader(workbook, sheet, contractHeaderArr);

		assetHeader(workbook, sheet, assetHeaderArr);
		//System.out.println("** Call loadWorkSheetContracts");
		loadWorkSheetContracts(workbook, sheet, list);
		//System.out.println("** Call loadWorkSheetAssets");
		loadWorkSheetAssets(workbook, sheet, list);
		//System.out.println("** Call Write Excel");
		// System.out.println("** Call loadWorkSheet");
		// WriteExcel.loadWorkSheet(workbook, sheet, strArr, 1, ";");
		// BufferedInputStream in = null;

		try {
			// HttpServletResponse response = getResponse(); // get ServletResponse
			res.setContentType("application/vnd.ms-excel"); // Set up mime type
			res.addHeader("Content-Disposition", "attachment; filename=" + FILE_NAME);
			OutputStream out2 = res.getOutputStream();
			workbook.write(out2);
			out2.flush();

			// ********************************************************************************************************************************

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		fileHandler.flush();
		fileHandler.close();
	} // End doGet()

} // End Class
