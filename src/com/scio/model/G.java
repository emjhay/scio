package com.scio.model;

import java.util.Timer;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;

import com.scio.database.DataBaseManager;

public class G {
	   public static final String TAB_A  = "tab_a_identifier";
	   public static final String TAB_B  = "tab_b_identifier";
	   public static final String TAB_C  = "tab_c_identifier";
//	   public static final String TAB_D  = "tab_d_identifier";

	   //Your other constants, if you have them..
	   
	   public static boolean RESPONSE;
	   
	   public static final String ID = "userKey";
	   public static final String ADMIN_ADMIN = "admin";
	   
	   public static final String USERNAME = "userN";
	   public static final String FIRSTNAME = "fname";
	   public static final String LASTNAME = "lname";
	   public static final String EMAIL = "email";
	   public static final String AEMAIL = "aemail";
	   public static final String CEMAIL = "cemail";
	   public static final String CPASS = "cpass";
	   public static final String PASSWORD = "passW";
	   public static final String PHONENUMBER = "pnumber";
	   public static final String APHONENUMBER = "APhoneN";
	   public static final String CPHONENUMBER = "phoneN";
	   
	   public static final String LOCATION_INTERVAL = "locInt";
	   public static final String SMS_INTERVAL = "smsInt";
	   public static final String CALL_INTERVAL = "callInt";
//	   public static final String PHOTO_INTERVAL = "photoInterval";
//	   public static final String STATUS = "status";
	   public static final String LOCATION_STATUS = "loc";
	   public static final String SMS_STATUS = "sms";
	   public static final String CALL_STATUS = "calls";
	   
	   public static final String USERTYPE = "userType";
	   public static final String VERIFIED = "verified";
	   public static final String EXPIRY = "expiry";
	   
//	   public static final String URL = "http://172.17.1.158:8080/LifeSave/webresources/";
//	   public static final String URL = "http://192.168.1.167:8080/LifeSave/webresources/";
//	   public static final String URL = "http://192.168.1.103:8080/LifeSave/webresources/";
	   public static final String URL = "http://192.168.100.85:8080/SCIO/webresources/";

	   public static final String ADMIN = "model.tbladmin/";
	   public static final String CLIENT = "model.tbluser/";
	   public static final String CREATE_ADMIN = "createAdmin/";
	   public static final String CREATE_CLIENT = "createClient/";
	   public static final String FINDBY_USERNAME = "findAdminByUsername/";
	   public static final String LOGINWITH_USERTYPE = "loginByUserType/";
	   public static final String FINDCLIENTBY_ADMIN = "findClientByAdmin/";
	   public static final String DELETE_CLIENT = "removeClient/";
	   public static final String DELETE_ADMIN = "removeAdmin/";
	   public static final String EDIT_CLIENT = "editClient/";
	   public static final String EDIT_ADMIN = "editAdmin/";
	   
	   public static boolean CHECK_FIRSTNAME;
	   public static boolean CHECK_LASTNAME;
	   public static boolean CHECK_USERNAME;
	   public static boolean CHECK_EMAIL;
	   public static boolean CHECK_CLIENT_EMAIL;
	   public static boolean CHECK_CLIENT_PASS;
	   public static boolean CHECK_PASSWORD;
	   public static boolean CHECK_CONFIRM;
	   public static boolean CHECK_PHONENUMBER;
	   public static boolean CHECK_ADMIN_PHONENUMBER;
	   
	   
	   public static int ADMIN_ID = 0;
	   public static String ADMIN_FIRSTNAME = "";
	   public static String ADMIN_LASTNAME = "";
	   public static String ADMIN_USERNAME = "";
	   public static String ADMIN_PASSWORD = "";
	   public static String ADMIN_EMAIL = "";
	   public static String ADMIN_PHONENUMBER = "";
//	   public static String ADMIN_EXPIRY = "";
	   
	   public static int CLIENT_ID = 0;
	   public static String CLIENT_USERNAME = "";
	   public static String CLIENT_PASSWORD = "";
	   public static String CLIENT_FIRSTNAME = "";
	   public static String CLIENT_LASTNAME = "";
	   public static String CLIENT_PHONENUMBER = "";
	   public static int CLIENT_LOCATION_INTERVAL = 0;
	   public static int CLIENT_SMS_INTERVAL = 0;
	   public static int CLIENT_CALL_INTERVAL = 0;
	   public static int CLIENT_PHOTO_INTERVAL = 0;
//	   public static int CLIENT_STATUS = 0;
	   public static String CLIENT_ADMIN_EMAIL = "";
	   public static String CLIENT_CLIENT_EMAIL = "";
	   public static String CLIENT_PASS_EMAIL = "";
	   public static String CLIENT_ADMIN = "";
	   public static String CLIENT_USERTYPE = "";
	   public static int CLIENT_LOCATION_STATUS = 0;
	   public static int CLIENT_SMS_STATUS = 0;
	   public static int CLIENT_CALL_STATUS = 0;
	   public static String CLIENT_EXPIRY = "";
	   public static String CLIENT_ADMIN_PHONE = "";
	   
	   
	   // DATABASE LOCAL
	   
	   public static DataBaseManager DATA_BASE;
	   public static Context CONTEXT;
	   
	   public static final String LOCATION_TABLE = "location";
	   public static final String SMS_TABLE = "sms";
	   public static final String CALL_TABLE = "call";
	   public static final String PHOTO_TABLE = "photo";
	  
	   
	   public static final String SET_INTERVAL = "set_interval";
	   public static final String CURRENT_HOUR = "current_hour";
	   public static final String EMAIL_STATUS = "email_status";
	   
	   public static final String EMAIL_STORAGE_TABLE = "email_storage";
	   
//	   public static final String EMAIL_STORAGE_ID = "id";
//	   public static final String EMAIL_STORAGE_USERNAME = "username";
//	   public static final String EMAIL_STORAGE_USER_PASS = "user_pass";
//	   public static final String EMAIL_STORAGE_EMAIL = "email";
//	   public static final String EMAIL_STORAGE_EMAIL_PASS = "email_pass";
//	   public static final String EMAIL_STORAGE_EMAIL_STATUS = "email_status";
//	   public static final String EMAIL_STORAGE_PASS_STATUS = "pass_status";
	   
	   
	   
	   public static Handler HANDLER;
	   public static Timer TIMER_GLOBAL;
	   public static Handler HANDLER_GLOBAL;
	   
	   public static boolean SHOW_SCREEN_CLIENT;
	   public static boolean SHOW_SCREEN_CAMERA;
	   
	   public static int G_W;
	   public static int G_H;
//	   public static Bitmap PHOTO;
	   
	   public static String POPUP_EMAIL = "";
	   public static String POPUP_PASS = "";
	   public static String POPUP_HOST = "";
	   public static String POPUP_ALERT_MSG = "";
	   public static byte[] BYTE_ARRAY;
	   
//	   public static final String EXPIRY_TABLE = "expiry";
//	   
//	   public static final String EXPIRY_USERNAME = "username";
//	   public static final String EXPIRY_PASSWORD = "password";
//	   public static final String EXPIRY_USERTYPE = "usertype";
//	   public static final String EXPIRY_CREATE_DATE = "create_date";
//	   public static final String EXPIRY_EXPIRY_DATE = "expiry_date";
	   
//	   public static boolean CHECK_IF_EXPIRED;
	   public static String FORGOT_PASSWORD;
	   public static String FORGOT_USERNAME;
	   
	   public static Typeface FONT;
	   
}
