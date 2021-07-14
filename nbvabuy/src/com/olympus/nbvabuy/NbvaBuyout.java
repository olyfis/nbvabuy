package com.olympus.nbvabuy;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.NodeList;

//import com.olympus.nbva.DateUtil;
import com.olympus.nbva.assets.AssetData;
import com.olympus.nbva.contracts.ContractData;
import com.olympus.nbva.kits.GetKitData;
import com.olympus.nbva.nbvacode.NbvaCodeDisp;
import com.olympus.olyutil.Olyutil;
import com.olympus.nbva.contracts.CalcTableData;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.formula.functions.FinanceLib;
 
import com.olympus.olyutil.*;
import com.olympus.dateutil.DateUtil;
import com.olympus.olyutil.log.OlyLog;
//import com.olympus.nbva.DateUtil;

// Run: http://localhost:8181/nbvabuy/nbvabuy?id=101-0010311-004&eDate=2020-04-16
//http://cvyhj3a27/:8181/nbvabuy/nbvabuy?id=101-0010311-004&eDate=2020-04-16
@WebServlet("/nbvabuyout")
public class NbvaBuyout extends HttpServlet {

	static Statement stmt = null;
	static Connection con = null;
	static ResultSet res  = null;
	static NodeList  node  = null;
	static String s = null;
	static private PreparedStatement statement;
	static String propFile = "C:\\Java_Dev\\props\\unidata.prop";
	
	//static String sqlFile = "C:\\Java_Dev\\props\\sql\\NBVAassetList_V4.sql";
	//static String hdrFile = "C:\\Java_Dev\\props\\headers\\NBVA_Hdr_V4.txt";
	
	static String sqlFile = "C:\\Java_Dev\\props\\sql\\NBVAbuy\\NBVA_assetBuy_V6.sql";
	 static String hdrFile = "C:\\Java_Dev\\props\\headers\\NBVA\\NBVA_assetBuy_fullQueryHdr_v2.txt";
	static String kitFileName = "C:\\Java_Dev\\props\\kitdata\\kitdata.csv";
	//static boolean contractStat = false;
	//static boolean invoiceCodeStat = false;
	static String purchOption = "";
	static int mthRem = 0;
	
	/*****************************************************************************************************************************************************/
	
	/****************************************************************************************************************************************************/
	private final static Logger LOGGER = Logger.getLogger(NbvaBuyout.class.getCanonicalName());
	
	// location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "uploadDir";
 
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
	/****************************************************************************************************************************************************/

	public static ArrayList<String> getDbData(String id, String sqlQueryFile, String booked, String qType) throws IOException {
		FileInputStream fis = null;
		FileReader fr = null;
		String s = new String();
		String sep = "";
        StringBuffer sb = new StringBuffer();
        ArrayList<String> strArr = new ArrayList<String>();
		try {
			fis = new FileInputStream(propFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties connectionProps = new Properties();
		connectionProps.load(fis);
		fr = new FileReader(new File(sqlQueryFile));	
		// be sure to not have line starting with "--" or "/*" or any other non alphabetical character
		BufferedReader br = new BufferedReader(fr);
		while((s = br.readLine()) != null){
		      sb.append(s);       
		}
		br.close();
		//displayProps(connectionProps);
		String query = new String();
		query = sb.toString();	
		//System.out.println( query);	 
		try {
			con = Olyutil.getConnection(connectionProps);
			if (con != null) {
				//System.out.println("Connected to the database");
				statement = con.prepareStatement(query);
				//System.out.println("***^^^*** contractID=" + contractID);
				statement.setString(1, id);
				sep = ";";	 
				res = Olyutil.getResultSetPS(statement);		 	 
				strArr = Olyutil.resultSetArray(res, sep);			
			}		
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return strArr;
	}
	/*****************************************************************************************************************************************************/
	
	// String nm2 = addMonthsToDate("2021-05-07", 1);
	public static String addMonthsToDate(String origDate, int mths) {
		String newDate = "";
		LocalDate date   = LocalDate.parse(origDate); 
		LocalDate returnvalue  = date.plusMonths(mths); 
		
		 //System.out.println("LocalDate after " + " adding months: " + returnvalue); 
		 newDate = returnvalue.toString();
		 //System.out.println("***** LocalDate after " + " adding months: " + newDate); 
		
		return(newDate);
	}
	/*****************************************************************************************************************************************************/
	/*****************************************************************************************************************************************************/
	/*****************************************************************************************************************************************************/
	/*****************************************************************************************************************************************************/
	/*****************************************************************************************************************************************************/
	/*****************************************************************************************************************************************************/
	/*****************************************************************************************************************************************************/
	/*****************************************************************************************************************************************************/
	/*****************************************************************************************************************************************************/

	
	/*****************************************************************************************************************************************************/
	public static AssetData loadAssetObj(String[] line, HashMap<String, String> codeMap) {
		AssetData asset = new AssetData();
		double equipCost = Olyutil.strToDouble(line[20]);
		double residual = Olyutil.strToDouble(line[19]);
		String desc = line[10];
		String aID = "";
		
		if (desc.equals("EUA") || desc.equals("B/O")) {
			//System.out.println("***^^^*** AssetData: Desc="  +  desc );
			if (residual == 0.00 || equipCost == 0.00) {
				//System.out.println("***^^^*** AssetData: residual="  +  residual  + " -- EC=" + equipCost);
				asset = null;
				return(asset);
			}	
		}
		//JB Check
		if (desc.equals("Returned & Equipment Cost")) {   //  and rental amount == 0
			System.out.println("***^^*** AssetData (Desc):" + desc);
			
		}
		
		
		
		//System.out.println("***^^*** AssetData:" + line.toString() );
		//System.out.println("***^^^*** AssetData: L22="  +  line[22] + "-- Fmt" + Olyutil.strToInteger(line[22] ) );
		//System.out.println("***^^^*** AssetData: L11="  +  line[11] + "--"   );
		//System.out.println("***^^^*** AssetData: L12="  +  line[12] + "--"   );
		
		//System.out.println("***^^^*** AssetID=" + line6] +"--");
		
		 asset.setAssetId(Olyutil.strToLong(line[8]));
		 asset.setEquipType(line[9]); 
		 //asset.setCustomerID(line[9]); 
		 asset.setEquipDesc(desc); 
		 asset.setModel(line[11]); 
		 asset.setSerNum(line[12]); 
		 asset.setQty(Olyutil.strToInteger(line[13])); 
		 asset.setEquipAddr1(line[14]); 
		 asset.setEquipAddr2(line[15]); 
		 asset.setEquipCity(line[16]); 
		 asset.setEquipState(line[17]);
		 asset.setEquipZip(line[18]); 
		 
		 asset.setResidAmt(residual);
		 
		 asset.setEquipCost(equipCost);
		 
		 asset.setassetRentalAmt(Olyutil.strToDouble(line[21]));
		 asset.setOL_Residual(line[32]);
		 asset.setContract_Payment(Olyutil.strToDouble(line[33]));
		 asset.setAsset_CPP_Rate(line[37]);
		 asset.setAsset_Purchase_Option(line[40]);
		 //System.out.println("***^^^^^^*** Line 47=" + Olyutil.strToDouble(line[47]) + "--");
		 
		 //System.out.println("***^^^^^^*** DispCode=" + asset.getDispCode() + "-- Orig=" + line[22] + "-- Asset=" + asset.getAssetId() + "--");
	if (codeMap.size() > 0) {
		//System.out.println("*** IF Load -- File Uploaded -- CM=" + codeMap.get(line[7]) + "-- SZ=" + codeMap.size() + "-- Asset=" + asset.getAssetId() + "--");

		if (Olyutil.isNullStr(codeMap.get(line[7])) || codeMap.get(line[7]).equals("null")) {
			asset.setDispCode(-9);
			// System.out.println("***^^^^^^*** Set DispCode=" + asset.getDispCode() + "--
			// Orig=" + line[22] + "-- Asset=" + asset.getAssetId() + "--");
		} else {

			asset.setDispCode(Olyutil.strToInt(codeMap.get(line[7])));
		}
		//System.out.println("*** END IF Load -- File Uploaded -- CM=" + codeMap.get(line[7]) + "-- SZ=" + codeMap.size()+ "-- Asset=" + asset.getAssetId() + "--");
		
		
	} else {

		//System.out.println("***^^ LOAD Else^^^^*** DispCode=" + asset.getDispCode() + "-- Orig=" + line[22] + "-- Asset=" + asset.getAssetId() + "--");
		if (Olyutil.isNullStr(line[22]) || line[22].equals("null")) {
			asset.setDispCode(-9);
			// System.out.println("***^^^^^^*** Set DispCode=" + asset.getDispCode() + "--
			// Orig=" + line[22] + "-- Asset=" + asset.getAssetId() + "--");
		} else {
			asset.setDispCode(Olyutil.strToInteger(line[22]));
		}
		//System.out.println("***^^^LOAD Else SET^^^*** DispCode=" + asset.getDispCode() + "-- Orig=" + line[22] + "-- Asset="+ asset.getAssetId() + "--");

		// System.out.println("***^^^^^^*** Set DispCode=" + asset.getDispCode() +
		// "--");

	}
		 
		 
		 
		 return(asset);
	}
	
	
	
	/****************************************************************************************************************************************************/
	public static double roundTax(double equipCost, double rate) {
		double rnd = 0.00;
		
		rnd = Math.round(equipCost * ((rate * 100) / 100));
		rnd = rnd/100;
		
		return(rnd);
		
	}
	
	/****************************************************************************************************************************************************/

	public static void displayListValues(String[] list, int n) {
		for ( int i = 0; i<=n; i++ ) {
			System.out.println("***^ i=" + i + "-- listItem=" + list[i] + "--");
			
		}
	}

	
	
	
	

	/****************************************************************************************************************************************************/
	public static ContractData loadContractObj(String[] strSplitArr, String effectiveDate, String invNum, String invDate) {
		double transCityTaxRate = 0.00;
		double cityTaxRate = 0.00;
		double stateTaxRate = 0.00;
		double cntyTaxRate = 0.00;
		double transCntyTaxRate = 0.00;
		double cityTaxTotal = 0.00;
		double stateTaxTotal = 0.00;
		double cntyTaxTotal = 0.00;
		double transCntyTaxTotal = 0.00;
		double transCityTaxTotal = 0.00;
		double equipCost = 0.00;
		
		ContractData contract = new ContractData();
		//double servicePay = 0.0;
		//double equipPay = 0.0; 
		contract.setContractID(strSplitArr[0]); 
		contract.setCustomerID(strSplitArr[1]);
		contract.setCustomerName(strSplitArr[2]); 
		contract.setCommenceDate(strSplitArr[3]);
		contract.setTerm(Olyutil.strToLong(strSplitArr[4])); 
		contract.setTermDate(strSplitArr[5]); 
		equipCost = Olyutil.strToDouble(strSplitArr[6]);
		contract.setEquipRate(equipCost); 
		
		contract.setServiceRate(Olyutil.strToDouble(strSplitArr[7]));; 
		contract.setIlTermDate(strSplitArr[23]);
		contract.setContractStatus(strSplitArr[24]); 
		contract.setInvoiceCode(strSplitArr[25]); 
		contract.setPurOption(strSplitArr[26]); 
		contract.setEffectiveDate(effectiveDate);	 
		contract.setFinalInvDueDate(invDate);	// need to find correct value
		contract.setCustomerAddr1(strSplitArr[41]); 
		contract.setCustomerAddr2(strSplitArr[42]); 
		contract.setCustomerAddr3(strSplitArr[43]); 
		contract.setCustomerCity(strSplitArr[44]); 
		contract.setCustomerState(strSplitArr[28]);
		contract.setCustomerZip(strSplitArr[45]); 
		contract.setNextAgingDate(strSplitArr[46]);
		
		contract.setContractPayment(Olyutil.strToDouble(strSplitArr[33]));
		contract.setSold_to_Number(strSplitArr[29]);
		contract.setTerm_1(strSplitArr[30]);
		contract.setYield(Olyutil.strToDouble(strSplitArr[34]));
		contract.setProcedures_to_Date(Olyutil.strToInt(strSplitArr[35]));
		contract.setRemaining_Procedures(Olyutil.strToInt(strSplitArr[36]));
		contract.setNon_Reporting_Procedures(strSplitArr[38]);
		contract.setFIS_Rep(strSplitArr[39]);
		
		
		//System.out.println("***^^^Contract^^^*** Line 47=" + Olyutil.strToDouble(strSplitArr[47]) + "--");
		contract.setRemainRentRec(Olyutil.strToDouble(strSplitArr[47]));
		// added 2021-01-29
		cityTaxRate = Olyutil.strToDouble(strSplitArr[48]);
		contract.setCityTaxRate(cityTaxRate);
	
		cntyTaxRate = Olyutil.strToDouble(strSplitArr[49]);
		contract.setCntyTaxRate(cntyTaxRate);
		stateTaxRate = Olyutil.strToDouble(strSplitArr[50]);
		
		transCntyTaxRate = Olyutil.strToDouble(strSplitArr[51]);
		transCityTaxRate = Olyutil.strToDouble(strSplitArr[52]);
		
		
		contract.setTransCntyTaxRate2(transCntyTaxRate);
		contract.setTransCityTaxRate(transCityTaxRate);
		
		
		cityTaxTotal = roundTax(equipCost, cityTaxRate);
		stateTaxTotal = roundTax(equipCost, stateTaxRate);
		transCntyTaxTotal = roundTax(equipCost, transCntyTaxRate);
		cntyTaxTotal = roundTax(equipCost, cntyTaxRate);
		transCityTaxTotal = roundTax(equipCost, transCityTaxRate);
		
		contract.setStateTaxRate(stateTaxRate);
		
		contract.setCityTaxTotal(cityTaxTotal);
		contract.setStateTaxTotal(stateTaxTotal);
		contract.setCntyTaxTotal(cntyTaxTotal);
		contract.setTransCntyTaxTotal(transCntyTaxTotal);
		contract.setTransCityTaxTotal(transCityTaxTotal);
		//System.out.println("***^ TCR=" + transCityTaxRate + "-- TCT=" +  transCityTaxTotal + "--");
		//cntyTaxTotal = equipCost * ((cntyTaxRate / 100));
		
		//cityTaxTotal = equipCost * ((cityTaxRate / 100) );
		//stateTaxTotal = equipCost * ((stateTaxRate / 100));
		
		//transCntyTaxTotal = equipCost * ((transCntyTaxRate / 100));
		
		//System.out.println("***^ SSA48=" + Olyutil.strToDouble(strSplitArr[48]) + "-- 49=" +  Olyutil.strToDouble(strSplitArr[49])  
				//+ "-- 50=" + Olyutil.strToDouble(strSplitArr[50])  + "-- 51=" + Olyutil.strToDouble(strSplitArr[51]) + "-- 52=" + Olyutil.strToDouble(strSplitArr[52]) + "--" ) ;
	 
		//double totalTaxRate = Double.parseDouble(strSplitArr[48]) + Double.parseDouble(strSplitArr[49]) + Double.parseDouble(strSplitArr[50]) + Double.parseDouble(strSplitArr[51] + Double.parseDouble(strSplitArr[52]));
		// 2021-06-18 fixed double point error when 0.00 occurs
		double totalTaxRate = 0.00;
		
		for ( int i = 48; i<=52; i++ ) {
			
			double tr = Double.parseDouble(strSplitArr[i]);
			if (tr > 0) {
				totalTaxRate += tr;
			}
			
			
		}
		
		
		 //double totalTaxRate = Double.parseDouble(strSplitArr[48]) + Double.parseDouble(strSplitArr[49]) + Double.parseDouble(strSplitArr[50]) + Double.parseDouble(strSplitArr[51]     );
		//double totalTaxRate = Double.parseDouble(Olyutil.strToDouble(strSplitArr[48])) + Olyutil.strToDouble(strSplitArr[49]) + Olyutil.strToDouble(strSplitArr[50]) + Olyutil.strToDouble(strSplitArr[51] + Olyutil.strToDouble(strSplitArr[52]));
		
		contract.setTotalTaxRate(totalTaxRate);
		double invoicePayment_t = cityTaxTotal +  stateTaxTotal    +  cntyTaxTotal   + transCntyTaxTotal  + transCityTaxTotal + equipCost ;
		
		
		
		
		
		
		
		double invoicePayment  = Math.round(invoicePayment_t * 100);
		invoicePayment = invoicePayment/100;
		
		//System.out.println("***^^^ CityTT=" + cityTaxTotal  + "-- StateTT=" + stateTaxTotal  + "-- cntyTT=" + cntyTaxTotal +  "-- transCntyTT=" + transCntyTaxTotal + "--");
		//System.out.println("***^^^ContractTaxRate=" +  totalTaxRate + "-- EquipPayment=" + equipCost + "--PaymentWtax="   + invoicePayment + "--" );
		contract.setPaymentWtax(invoicePayment);
		contract.setInvoiceDueDate(invDate);
		contract.setInvoiceNum(invNum);
		// Done
		
		 //System.out.println("*** ContractData: 27" + strSplitArr[27] );
		
		//System.out.println("*** ContractData:" + strSplitArr.toString() );
		String effDatePlus30 = addMonthsToDate(invDate, 1);
		contract.setInvoiceDueDatePlus30(effDatePlus30);
		
		
		
		return(contract);
	}
	/****************************************************************************************************************************************************/
	public static  List<Pair<ContractData, List<AssetData> >> parseData(ArrayList<String> strArr, int sz, String effDate, HashMap<String, String> codeMap, String invNum, String invDate  ) {
		String[] strSplitArr = null;
		ContractData contract = null;
		AssetData asset = null;
		List<AssetData> assets = new ArrayList<AssetData>();
		List<Pair<ContractData, List<AssetData> >> listRtn = new ArrayList<>();
		
		boolean contractStat = false;
		boolean invoiceCodeStat = false;
		
		int i = 0;
		//System.out.println("*** (parseDat): -> SZ=" + sz );
		for (i = 0; i < sz; i++) {
			 //System.out.println("*** Data:" + strArr.get(i) );
			strSplitArr = Olyutil.splitStr(strArr.get(i), ";");
			
			
			purchOption = strSplitArr[40];	// Asset_Purchase_Option
			//System.out.println("*********** i=" + i + "-- Disp=" + strSplitArr[22] +    "-- Value=" + strSplitArr[i] );  
			if (i == 0) { // get Contract data
				
				//System.out.println("*********** i=" + i + "-- Line=" + strArr.get(i) + "--"); 
				//displayListValues(strSplitArr, 52);
				contract = loadContractObj(strSplitArr, effDate, invNum, invDate);
				
					asset = loadAssetObj(strSplitArr, codeMap);
				 
				if (strSplitArr[24].equals("03")) { // Contract_Status_Code
					contractStat = true;
					 //System.out.println("*** SC" + strSplitArr[24] + "--");
				}
				if (strSplitArr[25].equals("N")) { // Invoice_Code
					 //System.out.println("*** IC=" + strSplitArr[25] + "--");
					invoiceCodeStat = true;
				}			
			} else { // get Asset data && run checks	
				asset = loadAssetObj(strSplitArr, codeMap);
			}
			// Calculate floorPrice
			
			
				if (asset != null) {
					//System.out.println("*****PARSE******!!## DCODE=" + asset.getDispCode()    +   "-- AID=" + asset.getAssetId()  +  "--");
				assets.add(asset);	
			}
		}
		//org.apache.commons.lang3.tuple.MutablePair<ContractData, List<AssetData>> p = org.apache.commons.lang3.tuple.MutablePair.of(contract, assets);
		//
		//listRtn.add(p);	
		//listRtn.add(Pair.of(contract, assets));   
		listRtn.add(Pair.of(contract, assets));   
		//System.out.println("*** ContractReturn: ID=" + contract.getContractID() + "--");
		//System.out.println("*** ContractReturn: EquipCost=" + contract.getEquipPayment() + "--");
		//System.out.println("*** AssetReturn: SerNum=" + asset.getSerNum() + "--");
		return(listRtn); 
	}
	
	/****************************************************************************************************************************************************/

	
	public static void doAssetCheck(String termDate, String effDate, String termSpanDate) throws ParseException {
		int rtn = 0;
		 rtn = DateUtil.compareDates(effDate, termDate); // d1 < d2 returns -1 ; d1 > d2 returns 1; d1 == d2 returns 0
		 //System.out.println("*** RTN=" + rtn);	
	}
	/****************************************************************************************************************************************************/
	/****************************************************************************************************************************************************/
	/****************************************************************************************************************************************************/
	/**
	 * @throws IOException **************************************************************************************************************************************************/
	public static ArrayList<Integer> doCheckDates(List<Pair<ContractData, List<AssetData> >> rtnPair, String effDate, int mthSpan ) throws IOException {
		ArrayList<Integer> errIDArray = new ArrayList<>();
		boolean contractStat = false;
		  boolean invoiceCodeStat = false;
		int rtn = 0;
		int dayChkRtn = 0;
		//int mthRem = 0;	
		int rtnDate = -15;
		String termDate = rtnPair.get(0).getLeft().getTermDate();
		String commDate = rtnPair.get(0).getLeft().getCommenceDate();
		
		String nextAgingDate = rtnPair.get(0).getLeft().getNextAgingDate();
		String invDatePlus30 = rtnPair.get(0).getLeft().getInvoiceDueDatePlus30();
		String invDueDate = rtnPair.get(0).getLeft().getInvoiceDueDate();
		
		
		
		String  termPlusSpan = DateUtil.addMonthsToDate(termDate, mthSpan);
		//System.out.println("^^^^ termPlusSpan=" + termPlusSpan);
		//System.out.println("***^^^^^*** mthSpan=" + mthSpan + "-- TermDate=" + termDate + "-- eDate=" + effDate + "-- CommDate=" + commDate + "-- spanDatePlus9=" + termPlusSpan);
		// Check dates
		int mthDiff = DateUtil.differenceInMonths(effDate, nextAgingDate);			
		rtnPair.get(0).getLeft().setMonthsDiff(mthDiff);
		
		
		if (Olyutil.dateCompare(invDueDate, effDate, "yyyy-MM-dd") < 0  ) {
			errIDArray.add(-40);
			return(errIDArray);
		}
		
		if (effDate.equals("Click for Calendar") || Olyutil.isNullStr(effDate)   ) {
			errIDArray.add(rtnDate);
			return(errIDArray);
		}
		
		
		try {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			//Date d1 = f.parse(effDate);
			//Date d2 = f.parse(termDate);		
			mthRem = DateUtil.differenceInMonths(effDate, termDate);
			 //System.out.println("***^^ dateDiff=" + mthRem + "--");
			// p1 = effDate -- p2 = commDate -- effDate cannot be less than termDate
			rtn = DateUtil.compareDates(effDate, commDate);
			// System.out.println("***^^^^^*** tDate=" + termDate + "-- eDate=" +
			// eDateParamValue + "-- commDate=" + commDate);
			//System.out.println("*** RTN=" + rtn);
			if (rtn < 0) {
				  System.out.println("**^^** Error occured with date compares. -- Error:" + rtn);
				// System.out.println("***^^^^^*** tDate=" + termDate + "-- eDate=" +
				// eDateParamValue + "-- CommDate="
				// + commDate + "-- spanDatePlus9=" + termPlusSpan);
				errIDArray.add(rtn);
			}
			dayChkRtn = DateUtil.compareDateDays(effDate, commDate);
			if (dayChkRtn < 0) {
				errIDArray.add(dayChkRtn);
				  System.out.println("!!!**^^**!!! Error occurred with day match error yyyy-MM-dd. -- Error:" +  dayChkRtn);
			}
			/*if (invoiceCodeStat == true) {
				errIDArray.add(-5);
				// System.out.println("----- IC error");
			}
			if (invoiceCodeStat == true) {
				errIDArray.add(-10);
				// System.out.println("----- IC error");
			}
			*/
			if (rtnPair.get(0).getLeft().getContractStatus().equals("03")) { // Contract_Status_Code
				errIDArray.add(-5);
				 //System.out.println("*** SC" + strSplitArr[24] + "--");
			}
			if (rtnPair.get(0).getLeft().getInvoiceCode().equals("N")) { // Invoice_Code
				 //System.out.println("*** IC=" + strSplitArr[25] + "--");
				errIDArray.add(-10);
			}
			
			
			/*  2020-11-03 - check removed  because new code calculates effective date.
			// Check Next Aging date
			int rVal = DateUtil.compareDates(nextAgingDate, effDate);
			
				
			if (rVal == -1) {
				errIDArray.add(-25);
			} else if ( rVal == 0) {
				System.out.println("nextAgingDate and effective date are equal.");
			} else if ( rVal > 0) {
				System.out.println("nextAgingDate is greater than effective date. -- MthDiff=" + mthDiff + "--");
			}
			*/
			
			
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return(errIDArray);
	}
	/****************************************************************************************************************************************************/
	/****************************************************************************************************************************************************/

	// type - type (true=pmt at beginning of period, false=pmt at end of period)
	
		/*
		 *  pv(double r, double n, double y, double f, boolean t)
		 	r - rate
			term - num of periods(term)
			y - pmt per period
			f - future value
			t - type
			***************************************************************************************
			Rate = Yield
			Term = Months Remaining
			Payperiod = 0
			FV = Residual
			Type = False

		 */	
	public static  double getPV(double rate, double term, double numPymts, double residual, boolean type) {
		Double dRtn = 0.0;
		Double dVal = FinanceLib.pv(rate, term, numPymts, residual, type);
		 dRtn = Olyutil.roundDouble(dVal, "UP", "0.00");
		//dRtn = roundDouble(dVal, "DOWN", "0.00");	
		return (dRtn);	 
		}	 
	/****************************************************************************************************************************************************/
	public static String contractCalcs(String effDate, String termDate, String termPlusSpan, List<Pair<ContractData, List<AssetData> >> dataObj, HashMap<String, CalcTableData> cMap) {	
		// String effDate = "2020-08-01";
		// String termDate = "2020-01-01";
		// String termPlusSpan = "2020-09-01";
		String opt = "";
		int rtn = DateUtil.compareDates(effDate, termDate);
		int rtnSpan = DateUtil.compareDates(effDate, termPlusSpan);
		int rtn_eff_gt = DateUtil.compareDates(effDate, termDate); // rtn 1
		int rtn_eff_lt_t9 = DateUtil.compareDates(effDate, termPlusSpan); // rtn -1
		//System.out.println("***^^^^^*** tDate=" + termDate + "-- eDate=" + effDate + "-- spanDatePlus9=" + termPlusSpan);
		//System.out.println("***^^^^^*** rtn=" + rtn + "-- rtnSpan=" + rtnSpan + "--");
		if (rtn == -1) { // effDate < termDate)
			opt = "opt1";
			doCalcData(dataObj, "opt_1", cMap);
			//System.out.println("*** Opt 1 -- R=" + rtn + " Effective Date < Term Date");
		}
		//System.out.println("***^ rtn_eff_gt=" + rtn_eff_gt + "-- rtn_eff_lt_t9=" + rtn_eff_lt_t9 + "--");	
		// if (effdate > termDate and effDate < (Term Date + 9 Months) termPlusSpan = "2020-09-01"; effDate = "2020-08-01" termDate = "2020-01-01";
		if (rtn_eff_gt == 1 && rtn_eff_lt_t9 == -1) { 
			opt = "opt2";
			doCalcData(dataObj, "opt_2", cMap);
			//System.out.println("***^^^ Opt 2 ^^*** rtn_eff_gt=" + rtn_eff_gt + "-- rtn_eff_lt_t9=" + rtn_eff_lt_t9 + "--");
			//System.out.println("*** (effdate > termDate and effDate < (Term Date + 9 Months)");
		}
		// effDate > (Term Date + 9 Months)); effDate = "2021-08-01"; termDate = "2020-01-01"; termPlusSpan = "2020-09-01";
		if (rtnSpan == 1) { 
			opt = "opt3";
			doCalcData(dataObj, "opt_3", cMap);
			//System.out.println("^^^^ Opt 3 -- R=" + rtn + " effDate  > (Term Date + 9 Months)");
		}
		return(opt);
	}
	/****************************************************************************************************************************************************/
	// Option 1 -> Effective Date < Term Date
	// Option 2 -> Effective Date Is Between Term Date and Term Date + 9 Months
	// Option 3 -> Effective Date > (Term Date + 9 Months) 
	public static void  do__CalcData_ORIG(List<Pair<ContractData, List<AssetData> >> dataObj, String option) {
		List<AssetData> assets = new ArrayList<AssetData>();
		String purchOpt = "";
		long assetID = 0;
		double price = 0.00;
		double rentalAmt = 0.00; // payment per month
		double rate = 0.0725;
		double residual = 0.00;
		double pv = 0.00;
		double equipCost = 0.00;
		double buyOutTotal = 0.00;
		double rollTotal = 0.00;
		double rtnTotal = 0.00;
		
		int dispCode = 0;
		int k = 0;
		int rArrSZ = dataObj.get(0).getRight().size();
		//System.out.println("*** rArrSZ=" + rArrSZ + "--");
		 
		ContractData contract =  dataObj.get(0).getLeft();
		purchOpt = contract.getPurOption();
		assets = dataObj.get(0).getRight();
		for (k = 0; k < rArrSZ; k++) {	
			price = 0.00;
			rentalAmt = assets.get(k).getassetRentalAmt();
			assetID = assets.get(k).getAssetId();
			residual = assets.get(k).getResidAmt();
			dispCode = assets.get(k).getDispCode();
			equipCost = assets.get(k).getEquipCost();
			pv = getPV(rate, mthRem, rentalAmt, residual, false) ;
			double rollPrice = 0.00;
			double buyPrice = 0.00;
			double rtnPrice = 0.00;
			
			
				if (purchOpt.equals("01"))  { //  ($1.00 Buyout) // Only care about $1.00 buyout
					//rollPrice = (mthRem * rentalAmt); // rollOver
					buyPrice = (mthRem * rentalAmt);
					//rtnPrice = (mthRem * rentalAmt);	
				} else {
					if (option.equals("opt_1")) { // within contractual term
					//System.out.println("*** OPT="  +  option + " -- ID="  +  assetID + " -- PO=" + purchOpt + "-- RA=" + rentalAmt + "-- dispCode=" + dispCode +   "-- PV=" + pv + "--");	
						if (residual > 0) { // Option 1	
							// rollover
							rollPrice = (mthRem * rentalAmt) + pv;
							// buyout
							buyPrice = (mthRem * rentalAmt) + (residual * 1.20);
							// return
							rtnPrice = (mthRem * rentalAmt);
					    } else if (residual == 0)  {					        
					    	rollPrice = (mthRem * rentalAmt) + 1.01;
						    buyPrice = (mthRem * rentalAmt) + 1.01;
						    rtnPrice = (mthRem * rentalAmt) + 1.01;			 
					    }
					} else if (option.equals("opt_2")) {	// less than 9 months in evergreen			
							if (residual > 0) { // Option 3
										rollPrice = (residual * 1.15);
										buyPrice = (residual * 1.20);
							  			rtnPrice = 0.00; 
							} else if (residual == 0)  {	
								rollPrice = equipCost * 0.10;
								buyPrice = equipCost * 0.10;	
								rtnPrice = 0.00;		
							}
					} else if (option.equals("opt_3")) { // in evergreen >= 9 months
						if (residual > 0) { // Option 3
							 
								rollPrice =  residual * ( 1.15 + (0.05 * mthRem));	
								buyPrice =  residual * ( 1.20 + (0.05 * mthRem));
								rtnPrice = 0.00;
							 	
					    } else if (residual == 0)  { 
					    	   rollPrice =  1.01;
					    	   buyPrice =  1.01;
							   rtnPrice = 0.00;	 		       
						}	 
					} // End opt_3
			} // end else
			//dataObj.get(0).getRight().get(k).setFloorPrice(price);
			dataObj.get(0).getRight().get(k).setBuyPrice(buyPrice);;
			buyOutTotal += buyPrice;
			rollTotal += rollPrice;
			rtnTotal += rtnPrice;
			dataObj.get(0).getRight().get(k).setRollPrice(rollPrice);;
			dataObj.get(0).getRight().get(k).setRtnPrice(rtnPrice);;
			//assets.add(k, element);
			//System.out.println("*** OPT="  +  option + " -- floorPrice=" +  price + "-- ID="  +  assetID + " -- PV=" + pv  + " -- PO=" + purchOpt + "-- RA=" + rentalAmt + "-- dispCode=" + dispCode   + "--");

		} // End for
		dataObj.get(0).getLeft().setBuyOut(buyOutTotal);
		dataObj.get(0).getLeft().setRollTotal(rollTotal);
		dataObj.get(0).getLeft().setRtnTotal(rtnTotal);
		
	}
	
	
	/****************************************************************************************************************************************************/

	public static HashMap<String, String> doLoadFormParams(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		HttpSession session = request.getSession();
		HashMap<String, String> paramMap = new HashMap<String, String>();
		String fileName = "";
		String filePath = "";
		// checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            PrintWriter writer = response.getWriter();
            writer.println("Error: Form must has enctype=multipart/form-data.");
            writer.flush();
            return(paramMap);
        }
 
        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // constructs the directory path to store upload file
        // this path is relative to application's directory
        //String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        String uploadPath = "C:\\tmp\\" + UPLOAD_DIRECTORY;
        
        //System.out.println("***^^^*** UploadPath=" + uploadPath);
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
		//****************************************************************************************************************************************/
      //****************************************************************************************************************************************/
        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            Iterator it = null;
            List<FileItem> formItems = upload.parseRequest(request);
            //String idName = (String) request.getAttribute("id");
            //String date2 = (String) request.getAttribute("date2");
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
            	
            	it = formItems.iterator();
            	//paramMap.put("filename", fileName);
            	while (it.hasNext()) {
    				FileItem item = (FileItem) it.next();
    				if (item.isFormField()) {
    					// Plain request parameters will come here.
    					String name = item.getFieldName();
    					String value = item.getString().trim();
    					// System.out.println("***^^*** Adding Name:" + name + "-- Value:" + value + "--");
    					paramMap.put(name, value);
    		
                       //System.out.println("***^^^*** Field:" + name + "-- Value:" + value + "-- FN=" + fileName + "-- FP=" + uploadPath);
    				} else {
						FileItem file = item;
						String fieldName = item.getFieldName();
						fileName = item.getName();
						String contentType = item.getContentType();
						boolean isInMemory = item.isInMemory();
						long sizeInBytes = item.getSize();
						// saves the file on disk
						//System.out.println("***^^^*** Name=" + item.getFieldName());
						fileName = new File(item.getName()).getName();
						filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(filePath);
						item.write(storeFile);
						//System.out.println("***!!!*** Add fileName to map:" + fileName + "--");	
						paramMap.put("filename", fileName);
						paramMap.put("filepath", filePath);
						request.setAttribute("message",
								"Upload has been done successfully! File located at: " + filePath);
						//System.out.println("*** ELSE: FN" + fileName);
						//System.out.println("***!!!*** fileName:" + fileName + "--");					
    				}
    			}
            }
            
            
        } catch (Exception ex) {
            request.setAttribute("message",
                    "There was an error: " + ex.getMessage());
        }
        //request.getSession().setAttribute("paramMap", paramMap);
        // redirects client to message page
       // getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
        
        return(paramMap);
	}
	/****************************************************************************************************************************************************/
	public static boolean doValidateParams(HashMap<String, String> paramMap) {
		boolean status = true;
		
		Set<String> keys = paramMap.keySet();  //get all keys
		for(String key: keys) {
			if (Olyutil.isNullStr(paramMap.get(key))) {
				status = false;
			} else if (key.equals("eDate")) {
				if (Olyutil.isNullStr(paramMap.get("eDate")) || paramMap.get("eDate").equals("Click for Calendar" ) ) {
					status = false;
				}
			}
		  //System.out.println("**----** Key=" + key + "-- Value=" + paramMap.get(key) + "--");
		}
		return(status);
		
	}
	/****************************************************************************************************************************************************/
	public static HashMap<String, String>  doReadCodeFile(String codeFile, String idVal) {
		ArrayList<String> codeArr = new ArrayList<String>();
		HashMap<String, String> codeMap = new HashMap<String, String>();
		String id = "";
		String asset = "";
		String dispCode = "";
		
		//System. out.println("***---***  ID=" +       idVal + "-- codeFile=" + codeFile);
		codeArr = Olyutil.readInputFile(codeFile);
		for (String str : codeArr) { // iterating ArrayList
			//System.out.println("**** Str=" + str);
			String[] items = str.split(",");
			id = items[0];
			asset = items[1];
			dispCode = items[2];
			if (idVal.equals(id)) {
				codeMap.put(asset, dispCode);
			}	
		}	
		//Olyutil.printStrArray(codeArr);

		return(codeMap);
	}
	/****************************************************************************************************************************************************/
	public static HashMap<String, Integer>  getCodesSQL(List<Pair<ContractData, List<AssetData>>> rtnPair) {
		HashMap<String, Integer> cMap = new HashMap<String, Integer>();
		int rtnArrSZ = rtnPair.get(0).getRight().size(); 
		 
		if (rtnArrSZ > 0) {
			//System.out.println("*** Asset=" + id + "-- DCODE=" + d  + "--sz=" + sz);	
			for (int k = 0; k < rtnArrSZ; k++) {	
				long assetId = rtnPair.get(0).getRight().get(k).getAssetId();
				int d = rtnPair.get(0).getRight().get(k).getDispCode();
				System.out.println("*** k=" +  k + "-- AssetID=" + assetId + "-- DispCode=" + d + "--");
				cMap.put(Long.toString(assetId), d);				
			}	
		} else {
			cMap = null;
		}
		return(cMap);
	}
	/****************************************************************************************************************************************************/
	public static HashMap<String, String>  getReturnStat(String statFile) {
		HashMap<String, String> rMap = new HashMap<String, String>();
		ArrayList<String> strArr = new ArrayList<String>();
		String key = "";
		String rVal = "";
		strArr = Olyutil.readInputFile(statFile);
		if (strArr.size() > 0) {
			for (String str : strArr) {
				String[] items = str.split(",");
				key = items[0];
				rVal= items[1];
				
				rMap.put(key, rVal);
			}
		} else {
			rMap = null;
		}
		
    
		return(rMap);
	}

/****************************************************************************************************************************************************************/

/****************************************************************************************************************************************************************/
	public static boolean displayHashMap(HashMap<String, String> hashMap) {
	boolean status = false;

	Set<String> keys = hashMap.keySet(); // get all keys
	if (keys.size() > 0) {
		status = true;
		for (String key : keys) {
			System.out.println("**----** Key=" + key + "-- Value=" + hashMap.get(key) + "--");
		}
	}
	return (status);
}

/****************************************************************************************************************************************************************/

	public static HashMap<String, CalcTableData> getCalcTableMap(ArrayList<String> sArr) {
	HashMap<String, CalcTableData> hMap = new HashMap<String, CalcTableData>();
		String mth = "";
		
		if (sArr.size() > 0) {		
			for (String str : sArr) { // iterating ArrayList
	 			CalcTableData calcTab = new CalcTableData();
				//System.out.println("**** Str=" + str);
				String[] items = str.split(",");
				if (items[0].equals("Months")) {
					continue;
				}
				mth = items[0];
				calcTab.setMonth(mth);
				calcTab.setBuy24plus(Olyutil.strToDouble(items[1]));
				calcTab.setRoll24plus(Olyutil.strToDouble(items[2]));
				calcTab.setBuy24(Olyutil.strToDouble(items[3]));
				calcTab.setRoll24(Olyutil.strToDouble(items[4]));
				hMap.put(mth, calcTab);
	 		}		
		} else {
			hMap = null;
		}
		
		
		return(hMap);
	}

/****************************************************************************************************************************************************************/

// Option 1 -> Effective Date < Term Date
		// Option 2 -> Effective Date Is Between Term Date and Term Date + 9 Months
		// Option 3 -> Effective Date > (Term Date + 9 Months) 
	// MOD_2020-06-02
		public static void  doCalcData(List<Pair<ContractData, List<AssetData> >> dataObj, String option, HashMap<String, CalcTableData> calcTableMap) {
			//System.out.println("The size of the calcTableMap is:" + calcTableMap.size()); 
			List<AssetData> assets = new ArrayList<AssetData>();
			String purchOpt = "";
			long assetID = 0;
			double price = 0.00;
			double rentalAmt = 0.00; // payment per month
			double rate = 0.0725;
			double residual = 0.00;
			double pv = 0.00;
			double equipCost = 0.00;
			double buyOutTotal = 0.00;
			double rollTotal = 0.00;
			double rtnTotal = 0.00;
			double buyResidualFactor = 0.00; // assigned from calcTable
			double rollResidualFactor = 0.00; // assigned from calcTable
			String key = "";
			
			int mr = 0;
			int dispCode = 0;
			int k = 0;
			int rArrSZ = dataObj.get(0).getRight().size();
			//System.out.println("*** rArrSZ=" + rArrSZ + "--");
			double cityTaxRate = 0.00;
			double stateTaxRate = 0.00;
			double cntyTaxRate = 0.00;
			double transCntyTaxRate = 0.00;
			double totalTaxRate = 0.00;
			ContractData contract =  dataObj.get(0).getLeft();
			purchOpt = contract.getPurOption();
			assets = dataObj.get(0).getRight();
			
			long term = contract.getTerm();
			
			cityTaxRate = contract.getCityTaxRate();
			stateTaxRate = contract.getStateTaxRate();
			cntyTaxRate = contract.getCntyTaxRate();
			transCntyTaxRate = contract.getTransCntyTaxRate();
			totalTaxRate = contract.getTotalTaxRate();
			
			//System.out.println("*** Total TaxRate="  + totalTaxRate + "-- CityTax="  + cityTaxRate + "-- StateTax=" + stateTaxRate + "-- CntyTax=" + cntyTaxRate + "-- TCntyTax=" + transCntyTaxRate + "--");
			
			//System.out.println("*** MTHREM=" + mthRem + "--");
			if (mthRem <= 0) { // in EverGreen
				key = Integer.toString(Math.abs(mthRem));
				if (term <= 24) {
					buyResidualFactor = calcTableMap.get(key).getBuy24() * 0.01;
					//mr = Math.abs(mthRem);
				} else {
					
					buyResidualFactor = calcTableMap.get(key).getBuy24plus() * 0.01;
				} 
			} else {
				key = "0";
				//System.out.println("***!!***  KEY=" + key + "-- Term="  + term + "-- BRF="  + buyResidualFactor);

				//buyResidualFactor = calcTableMap.get(key).getBuy24plus();
			}
			
		/*	
			System.out.println("***  KEY=" + key + "-- Term="  + term + "-- BRF="  + buyResidualFactor);
			System.out.println("*** KEY=" + key + "-- TermRemain="  + term + "-- MR=" + mr);
			System.out.println("*** mthRem:" + mthRem + "--");
			System.out.println("*** getBuy24:" + calcTableMap.get(key).getBuy24() + "-- Key=" + key); // <= 24
			System.out.println("*** getBuy24plus:" + calcTableMap.get(key).getBuy24plus() + "-- Key=" + key); //  > 24
			System.out.println("*** getRoll24:" + calcTableMap.get(key).getRoll24() + "-- Key=" + key);
			System.out.println("*** getRoll24plus:" + calcTableMap.get(key).getRoll24plus() + "--Key=" + key);
			*/
			for (k = 0; k < rArrSZ; k++) {	
				price = 0.00;
				rentalAmt = assets.get(k).getassetRentalAmt();
				assetID = assets.get(k).getAssetId();
				residual = assets.get(k).getResidAmt();
				dispCode = assets.get(k).getDispCode();
				equipCost = assets.get(k).getEquipCost();
				pv = getPV(rate, mthRem, rentalAmt, residual, false) ;
				double rollPrice = 0.00;
				double buyPrice = 0.00;
				double rtnPrice = 0.00;
				
				/***********************************************************************************************************************************************************/
				 
					if (purchOpt.equals("01"))  { //  ($1.00 Buyout)
						rollPrice = (mthRem * rentalAmt); // rollOver
						buyPrice = (mthRem * rentalAmt);
						rtnPrice = (mthRem * rentalAmt);	
					}  /* else {
						if (option.equals("opt_1")) { // within contractual term
						//System.out.println("*** OPT="  +  option + " -- ID="  +  assetID + " -- PO=" + purchOpt + "-- RA=" + rentalAmt + "-- dispCode=" + dispCode +   "-- PV=" + pv + "--");	
							if (residual > 0) { // Option 1	
								// rollover
								rollPrice = (mthRem * rentalAmt) + pv;
								// buyout
								buyPrice = (mthRem * rentalAmt) + (residual * buyResidualFactor);
	//buyPrice = (mthRem * rentalAmt) + (residual * residualFactor);  // new calc
								// return
								rtnPrice = (mthRem * rentalAmt);
						    } else if (residual == 0)  {					        
						    	rollPrice = (mthRem * rentalAmt) + 1.01;
							    buyPrice = (mthRem * rentalAmt) + 1.01;
							    rtnPrice = (mthRem * rentalAmt) + 1.01;			 
						    }
						} else if (option.equals("opt_2")) {	// less than 9 months in evergreen			
								if (residual > 0) { // Option 3
											rollPrice = (residual * rollResidualFactor);
											buyPrice = (residual * buyResidualFactor);
								  			rtnPrice = 0.00; 
								} else if (residual == 0)  {	
									rollPrice = equipCost * 0.10;
									buyPrice = equipCost * 0.10;	
									rtnPrice = 0.00;		
								}
						} else if (option.equals("opt_3")) { // in evergreen >= 9 months
							if (residual > 0) { // Option 3
								
									rollPrice =  residual * ( rollResidualFactor );	
									buyPrice =  residual * ( buyResidualFactor );
									rtnPrice = 0.00;
									System.out.println("***OPT3 -> Residual=" + residual   + "-- buyResidualFactor=" + buyResidualFactor + "-- BuyPrice=" + buyPrice + "--");
								 	
						    } else if (residual == 0)  { 
						    	System.out.println("***OPT3  in opt 3 else");
						    	   rollPrice =  1.01;
						    	   buyPrice =  1.01;
								   rtnPrice = 0.00;	 		       
							}	 
						} // End opt_3
				} // end else
					
				*/	
				//dataObj.get(0).getRight().get(k).setFloorPrice(price);
				dataObj.get(0).getRight().get(k).setBuyPrice(buyPrice);;
				buyOutTotal += buyPrice;
				rollTotal += rollPrice;
				rtnTotal += rtnPrice;
				dataObj.get(0).getRight().get(k).setRollPrice(rollPrice);;
				dataObj.get(0).getRight().get(k).setRtnPrice(rtnPrice);;
				//assets.add(k, element);
				//System.out.println("*** OPT="  +  option + " -- floorPrice=" +  price + "-- ID="  +  assetID + " -- PV=" + pv  + " -- PO=" + purchOpt + "-- RA=" + rentalAmt + "-- dispCode=" + dispCode   + "--");
				//System.out.println("***^^*** BP=" + dataObj.get(0).getRight().get(k).getBuyPrice() + "--");
			} // End for
			
			double taxRate = dataObj.get(0).getLeft().getTotalTaxRate();
			double buyOutData = dataObj.get(0).getLeft().getBuyOut();
			double buyTotalTaxes_t = 0.00;
			double buyTotalTaxes = 0.00;
			
			if( totalTaxRate <= 0) {
				buyTotalTaxes = buyOutTotal;
				
			}  else {
			 buyTotalTaxes_t = (totalTaxRate/100) * buyOutTotal;
			
			 buyTotalTaxes = buyTotalTaxes_t + buyOutTotal;
			
			}
			
			//System.out.println("***^^*** TR=" +totalTaxRate +  "-- BTT="  + buyTotalTaxes_t  + "--Buyout=" + buyOutTotal + "-- TaxedTotal=" + buyTotalTaxes + "--");
			dataObj.get(0).getLeft().setBuyOutWithTax(buyTotalTaxes);
			
			 
			dataObj.get(0).getLeft().setBuyOut(buyOutTotal);
			dataObj.get(0).getLeft().setRollTotal(rollTotal);
			dataObj.get(0).getLeft().setRtnTotal(rtnTotal);
			
		}
		
/****************************************************************************************************************************************************************/
		public static void displayData(ArrayList<String> strArr) {
			ArrayList<String> hdrArr = new ArrayList<String>();
			
			hdrArr = Olyutil.readInputFile(hdrFile);
			int k = 0;
			for (String str : strArr) { // iterating ArrayList
		 			
					//System.out.println("**** Str=" + str);
					String[] items = str.split(";");
					int sz = items.length;
					System.out.println("**** SZ=" + sz);
					for (int i = 0; i < sz; i++) {
						
						//System.out.println("****Row="  + k + "--  i=" + i + "-- " + hdrArr.get(i)  + "=" + items[i]);
						System.out.println(k + ";" + i + ";" + hdrArr.get(i)  + ";" + items[i]);
					}
					k++;
			}
			
		}
		
/****************************************************************************************************************************************************************/
		public static void displayDataMap(Map<String, String> map) {
		 

				Map<String, String> treeMap = new TreeMap<>(map); // sort hash by key
				for (Map.Entry<String, String> entry : treeMap.entrySet()) {
					System.out.println("*** Key:" + entry.getKey() + " --> Value:" + entry.getValue() + "--");
				}
				System.out.println("*****************************************************************************************************");
		
		}	
		/****************************************************************************************************************************************************************/
		
		private Map<String, String> getRequestHeadersInMap(HttpServletRequest request) {

	        Map<String, String> result = new HashMap<>();

	        Enumeration headerNames = request.getHeaderNames();
	        while (headerNames.hasMoreElements()) {
	            String key = (String) headerNames.nextElement();
	            String value = request.getHeader(key);
	            result.put(key, value);
	        }

	        return result;
	    }
	/****************************************************************************************************************************************************************/

	public static void displayDataMapSD(Map<String, Double> map) {

		for (Map.Entry<String, Double> entry : map.entrySet()) {
			System.out.println("*** Key:" + entry.getKey() + " --> Value:" + entry.getValue() + "--");
		}
		System.out.println("*****************************************************************************************************");

	}
		
		
	/**
	 * @throws ParseException **************************************************************************************************************************************************/
		
	/* process data from dailyAging file */
	public static HashMap<String, String> getInvoiceDates(String id, ArrayList<String> strArr, String sep) throws ParseException {
			HashMap<String, String> invoiceMap = new HashMap<String, String>();
			String invoiceNum= "";
			double sum = 0.0;
			double invTotal = 0.0;
			String dueDate = "";
			String invNum = "";
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
			LocalDateTime now = LocalDateTime.now();  
		    String currDate = dtf.format(now);
			//System.out.println("Begin ProcessInvoice:" + currDate);
			
			
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
		      Date cDate = sdformat.parse(currDate);
		      
		      
		      Date dDate = null;
			 //Olyutil.printStrArray(strArr, "A: ");
			 int i = 1;
			for (String s : strArr) {	
				String[] items = s.split(sep);	
				 //System.out.println("*** SZ=" + items.length + "-- " + s );
				dueDate = items[5];
				invNum = items[6];
				dDate = sdformat.parse(dueDate);
				 
				String contractID = items[0].trim();
				double val = 0.0;
				val = Olyutil.strToDouble(items[4]);	
				invoiceNum = items[6];
				/*
				if (i++ < 3) {
					System.out.println("*** ID=" + id + "-- ContractID=" + contractID +"--DueDate--" + dueDate + "--");

				}
				
				if(cDate.compareTo(dDate) <= 0) {
			        System.out.println("**** Date Valid: " + dueDate + "--");
			    } //else
			        //System.out.println("Date before: " + dueDate + "--");
				
				*/
				
				if (id.equals(contractID) && cDate.compareTo(dDate) <= 0) { // compare current date with invoice due date
					//System.out.println("*** ID=" + id + "-- ContractID=" + contractID +"--DueDate--" + dueDate + "--InvNum=" + invNum + "--");
					SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
					String dueDate_t = fmt.format(dDate);
 					invoiceMap.put(invoiceNum, dueDate_t);
				
					//System.out.println("*** Match:" + id + " -- Value:" + val  + "-- dueDate=" + dueDate_t + "--SZ=" + invoiceMap.size() + "--");
					 
				}  
				sum = 0.00;
			}	
			 
			
			return(invoiceMap);
		}
		
		
		/****************************************************************************************************************************************************/

		/****************************************************************************************************************************************************/
		public static double getContractTotals(String id, ArrayList<String> strArr, String sep) {
			double sum = 0.0;
			//Olyutil.printStrArray(strArr, "A: ");
			for (String s : strArr) {	
				String[] items = s.split(sep);	
				//System.out.println("*** SZ=" + items.length + "-- " + s );
				String contractID = items[0];
				double val = 0.0;
				val = Olyutil.strToDouble(items[4]);	
				 //System.out.println("*** ID=" + id + "-- ContractID=" + contractID +"--");
				if (id.equals(contractID)) {
					// System.out.println("*** Match:" + id + " -- Value:" + val );
					sum += val;
				}	
			}	
			return(sum);
		}
		/****************************************************************************************************************************************************/
		/*******************************************************************************************************************************************/
		// fmt "yyyy-MM-dd"
			// Invoke: String date2 = dateShift("2020-10-20", "yyyy-MM-dd","MM-dd-yyyy", -10);
			public static String dateShift(String origDate, String fmtIn, String fmtOut, int offset) {
		 
				DateTimeFormatter formatterIn = DateTimeFormatter.ofPattern(fmtIn);
				DateTimeFormatter formatterOut = DateTimeFormatter.ofPattern(fmtOut);
				LocalDate modDate = LocalDate.parse(origDate, formatterIn);
				LocalDate newDate = modDate.plusDays(offset);
				String rtnStr = formatterOut.format(newDate);
				
				return (rtnStr);
			}
	/****************************************************************************************************************************************************/
			// Exec: diffDays = diff2Dates(  baseCommDate, date30 );
			public static long diff2Dates(String today, String newEffDate )  {
			
				SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
				long diff = 0;
				long diffDate = 0;
				
				

				try {
					//Date date1 = myFormat.parse(today);
					Date date1 = myFormat.parse(today);
					Date date2 = myFormat.parse(newEffDate);
					diff = date2.getTime() - date1.getTime();
					
					diffDate = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
					//System.out.println("***---*** Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 
			return(diffDate);
			}

			
		
	/****************************************************************************************************************************************************/
	
		// Run: http://localhost:8181/nbvabuy/nbvabuy?id=101-0010311-004&eDate=2020-04-16

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap<String, CalcTableData> calcTableMap = new HashMap<String, CalcTableData>();
		HashMap<String, String> returnMap = new HashMap<String, String>();
		HashMap<String, String> paramMap = new HashMap<String, String>();
		HashMap<String, String> codeMapRtn = new HashMap<String, String>();
		HashMap<String, Integer> codeMapSQL = new HashMap<String, Integer>();
		HashMap<String, ArrayList<Integer>> sqlErrMap = new HashMap<String, ArrayList<Integer>>();
		
		  

		//HashMap<String, Double> invoiceTotalsMap = new HashMap<String, Double>();
		HashMap<String, String> invoiceDatesMap = new HashMap<String, String>();
		
		String newDate2 =  (String) request.getAttribute("newEffDate");
		
		sqlErrMap.clear();
		ArrayList<Integer> errIDArrayRtn = new ArrayList<>();
		//System.out.println("***Before***errIDArrayRtnAZ=" + errIDArrayRtn.size()  + "-- sqlErrMap" + sqlErrMap.size() + "--");
		errIDArrayRtn.clear();
		//System.out.println("***After***errIDArrayRtnAZ=" + errIDArrayRtn.size()  + "-- sqlErrMap" + sqlErrMap.size() + "--");
		ArrayList<String> ageArr = new ArrayList<String>();
		ArrayList<String> calcArr = new ArrayList<String>();
		ArrayList<String> codeArrRtn = new ArrayList<String>();
		double sumTotal = 0.0;
		String effDate = "";
		ContractData contractData = new ContractData();
		AssetData assetData = new AssetData();
		List<Pair<ContractData, List<AssetData>>> rtnPair = new ArrayList<>();
		int rtnArrSZ = 0;
		ArrayList<String> strArr = new ArrayList<String>();
		ArrayList<String> kitArr = new ArrayList<String>();
		boolean uploadFile = false;
		String useCodeData = "false";
		String idVal = "";
		//String dispatchJSP = "/nbvabuydetail_update.jsp";
		//String dispatchJSP = "/nbvabuyout_update.jsp";
		String dispatchJSP = "/nbvadetail_buy_result.jsp";
		String dispatchJSP_Error = "/nbvaerror.jsp";
		//String ageFile = "Y:\\GROUPS\\Global\\BI Reporting\\Finance\\FIS_Bobj\\unappsuspense\\dailyAge.csv";
		String ageFile = "C:\\Java_Dev\\props\\nbvaupdate\\dailyAge.csv";
		String rtnFile = "C:\\Java_Dev\\props\\nbvaupdate\\returnStat.csv";
		String calcFile = "C:\\Java_Dev\\props\\nbvaupdate\\calcTable.csv";
		String tag = "csvData: ";
		
		String logFileName = "nbvabuy.log";
		String directoryName = "D:/javalogs/logfiles/nbvabuy";
		Handler fileHandler =  OlyLog.setAppendLog(directoryName, logFileName, LOGGER );
		Date logDate = Olyutil.getCurrentDate();
		String dateFmt = Olyutil.formatDate("yyyy-MM-dd hh:mm:ss.SSS");
		String invDueDate = "";
		String invNumber = "";
		
		DecimalFormat format = new DecimalFormat("0.00");
		
		
		
		//System.out.println("**** -- NewEffDate="   + newDate2  +   "--");
		
		//String datePlus30 = dateShift(effDate, "yyyy-MM-dd","yyyy-MM-dd", 30);			
		//System.out.println("**** -- NewEffDate="   + effDate  +  "--D+30="   + datePlus30  + "--");
	
		
		
		
		
		
		
		
		Date bd = Olyutil.getCurrentDate();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String boDate = formatter.format(bd);
		paramMap = doLoadFormParams(request, response );
	
		/*
		Map<String, String> result = new HashMap<>();
		result = getRequestHeadersInMap(request);	
		displayDataMap(result);
	*/	
		String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		if (ipAddress == null) {  
		    ipAddress = request.getRemoteAddr();  
		}
		
		
		if (paramMap != null) {
			boolean stat = doValidateParams( paramMap);
			if (stat) {
				
				String currDate = java.time.LocalDate.now().toString();
				//System.out.println("** CurrDate=" + currDate);
				
			 
				//DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String newDate = "";
				try {
					newDate = com.olympus.dateutil.DateUtil.getNewEffectiveDate("2020-03-10");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 
				//System.out.println("*** NewEffectiveDate=" + newDate );
				
				calcArr = Olyutil.readInputFile(calcFile);
				calcTableMap = getCalcTableMap(calcArr);
				
				
				// check for 30 day lead time
				 effDate = paramMap.get("eDate");
				 idVal = paramMap.get("id");
				 String commDateOrig = paramMap.get("commDateOrig");
				 invDueDate = paramMap.get("invDueDate");
				 invNumber = paramMap.get("invNumber");
				 
				 
				 
				 
				 //long diffDays = diff2Dates(  commDateOrig, effDate); // original 301 day check
				 
				 // Calc DiffDays
				 long diffDays = diff2Dates(  invDueDate, effDate);
				 //System.out.println("!!**^^** invDueDate=" + invDueDate + "--effDate=" + effDate + "--commDateOrig=" + commDateOrig + "--DiffDays=" + diffDays + "--" );

				// System.out.println("!!**^^** eDate=" + effDate + "--***** commDateOrig="  +  commDateOrig   + "--Diffdays=" + diffDays + "--");
				 if (diffDays > 31) {
					 //System.out.println("***** Error: Past 30 day window for buyout."); 
					 request.getSession().setAttribute("dateErr2", "Error: Past 30 day window for buyout. -- Set date to: " + invDueDate + "  and try again.");
					 request.getRequestDispatcher(dispatchJSP_Error).forward(request, response);
					 return;
				 }
				 
				request.getSession().setAttribute("paramMap", paramMap);
				//System.out.println("!!**^^** Date=" + paramMap.get("eDate"));
				//System.out.println("!!**^^** ID=" + paramMap.get("id"));
				//System. out.println("!!**^^**  Invoice=" + paramMap.get("invoice"));
				if (! Olyutil.isNullStr(paramMap.get("filename"))) {
					String filePath = paramMap.get("filepath");
					uploadFile = true;
					
					
					//System.out.println("!!**^^**  FileName=" + paramMap.get("filename") + "-- SZ=" + sz);
					//System.out.println("!!**^^**  FilePath=" + filePath); 
					codeMapRtn = doReadCodeFile(filePath, idVal);
					int sz = codeMapRtn.size();
					if (sz > 0) {
						 //dispatchJSP = "/nbvadetail_code_result.jsp";
						useCodeData = "true";
					}
					
				} else {
					//System.out.println("!!**^^**  Error: FileName=" + paramMap.get("filename"));
					
				}
				//System. out.println("!!**^^**  FN=" + paramMap.get("filename"));
			} else {
				//System.out.println("!!**^^** STAT=false" );
				request.getRequestDispatcher(dispatchJSP_Error).forward(request, response);
			}	
			
			
			
			
			/***************************************************************************************************************************************************************/
			
			String formUrl = "formUrl";
			String formUrlValue = "/nbvabuy/nbvabuyexcel";
			request.getSession().setAttribute(formUrl, formUrlValue);
			//String formUrlDispValue = "/nbvacode/nbvadispfile"; // remove
			
			String formUrlDispValue = "/nbvabuy/nbvabuyexcel";
			request.getSession().setAttribute(formUrl, formUrlDispValue);
			String sep = ";";
			//String termPlusSpan = "";
			int mthSpan = 9;
			//int rtn = 0;
			//int dayChkRtn = 0;
			int arrSZ = 0;
			//int mthRem = 0;
			
			ageArr = Olyutil.readInputFile(ageFile);
			 
			returnMap = getReturnStat(rtnFile);
			//displayHashMap(returnMap);
			
			//System.out.println("*** Roll - 24plus (15) :" +  calcTableMap.get("15").getRoll24plus() + "--");
			
			// Olyutil.printStrArray(ageArr);
			// Olyutil.printStrArray(calcArr);
			// get data from DB
			strArr = getDbData(idVal, sqlFile, "", "Asset");			
			arrSZ = strArr.size();
			 //System.out.println("*** arrSz:" + arrSZ + "--");
			// Olyutil.printStrArray(strArr);
			//displayData(strArr);
		
			if (arrSZ > 0) {
				//Olyutil.printStrArray(strArr);
				
				 
				 
				kitArr = GetKitData.getKitData(kitFileName);
				// Olyutil.printStrArray(kitArr);
			
				rtnPair = parseData(strArr, arrSZ, effDate, codeMapRtn, invNumber, invDueDate );
				contractData = rtnPair.get(0).getLeft();
				rtnArrSZ = rtnPair.get(0).getRight().size(); 
				 //System.out.println("*** RTN Arr SZ=" + rtnArrSZ + "--");


				 
				// codeMapSQL = getCodesSQL(rtnPair);
				// System.out.println("*** ContractReturn: ID=" + contractData.getContractID() +
				// "--");
				// System.out.println("*** ContractReturn: EquipCost=" +
				// contractData.getEquipPayment() + "--");
				// request.getSession().setAttribute("contract", contractData);
				request.getSession().setAttribute("rtnPair", rtnPair);
				 //System.out.println("*** Get Contract Totals");
				// sumTotal = getContractTotals(idVal, ageArr, ";");
				 
				 try {
					invoiceDatesMap = getInvoiceDates(idVal, ageArr, ";"); // ageArr holds data from dailyAging file
					//displayDataMapSD(invoiceTotalsMap);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 
				 request.getSession().setAttribute("invoiceDatesMap", invoiceDatesMap);
				
				// System.out.println("***^^^^***** Get Contract Totals:" + sumTotal + "--");
				 errIDArrayRtn = doCheckDates(rtnPair, effDate, mthSpan);
				//System.out.println("----- dateErrors=" + errIDArrayRtn.size());
				String termDate = rtnPair.get(0).getLeft().getTermDate();
				String commDate = rtnPair.get(0).getLeft().getCommenceDate();
				//System.out.println("*** SumTotal=" + sumTotal );
				String termPlusSpan = DateUtil.addMonthsToDate(termDate, mthSpan);	
				String naDate = rtnPair.get(0).getLeft().getNextAgingDate();
				rtnPair.get(0).getLeft().setTermPlusSpan(termPlusSpan);
				request.getSession().setAttribute("commDate", commDate);
				 
				request.getSession().setAttribute("termDate", termDate);
				request.getSession().setAttribute("boDate", boDate);
				request.getSession().setAttribute("effDate", effDate);
				request.getSession().setAttribute("mthRem", mthRem);
				request.getSession().setAttribute("idVal", idVal);
				request.getSession().setAttribute("sumTotal", sumTotal);
				
				request.getSession().setAttribute("termPlusSpan", termPlusSpan);
				request.getSession().setAttribute("codeMapRtn", codeMapRtn);
				request.getSession().setAttribute("useCodeData", useCodeData);
				request.getSession().setAttribute("returnMap", returnMap);
				
				request.getSession().setAttribute("calcTableMap", calcTableMap);
			
				request.getSession().setAttribute("naDate", naDate);
				String userID = (String) request.getSession().getAttribute("username");
				
				LOGGER.info(dateFmt + ": " + "-- UserID:" +  userID   +  "-- Processing ID: " + idVal + "-- From: " + ipAddress    +   "--");
				
				
				//System.out.println("***!!!*** java -- Buy - 24plus (5):" +  calcTableMap.get("5").getRoll24plus() + "--");
				String opt = "";
				//System.out.println("***!!!***errIDArrayRtnAZ=" + errIDArrayRtn.size()  + "-- sqlErrMap" + sqlErrMap.size() + "--");
				if (errIDArrayRtn.size() > 0) {
					sqlErrMap.put(idVal, errIDArrayRtn);
					dispatchJSP = "/nbvaerror.jsp";	
				} else {			
					  opt = contractCalcs( effDate, termDate, termPlusSpan, rtnPair, calcTableMap);
				}	
				request.getSession().setAttribute("opt", opt);
				request.getSession().setAttribute("sqlErrMap", sqlErrMap);
				//System.out.println("*** Dispatch to:" + dispatchJSP);
				//System.out.println("*** Buy - 24plus (3):" +  calcTableMap.get("3").getBuy24plus() + "--");
				//System.out.println("The size of the calcTableMap is:" + calcTableMap.size()); 
				request.getRequestDispatcher(dispatchJSP).forward(request, response);
					
			} else {
				request.getRequestDispatcher(dispatchJSP_Error).forward(request, response);
			}
			
			
			
			/****************************************************************************************************************************************************************/
			
			
			
			
			
			
			
		} else {
			
			System.out.println("***===*** paramMap is null!");
		}
		fileHandler.flush();
		fileHandler.close();
	}
	
	/****************************************************************************************************************************************************/
	

}
