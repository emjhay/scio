package com.scio.model;

public class Users {

	private int id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private int locationInterval;
	private int smsInterval;
	private int callInterval;
	private String email;
	private String clientEmail;
	private String clientPass;
	private String admin;
	private String usertype;
	private int locationStatus;
	private int smsStatus;
	private int callStatus;
	private String expiry;
	private String adminPhone;
	
	public Users(int id, String username, String password, String firstName,
			String lastName, String phoneNumber, int locationInterval,
			int smsInterval, int callInterval, String email, String clientEmail, String clientPass,
			String admin, String usertype, int locationStatus, int smsStatus, int callStatus, String expiry, String adminPhone){
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.locationInterval = locationInterval;
		this.smsInterval = smsInterval;
		this.callInterval = callInterval;
		this.email = email;
		this.clientEmail = clientEmail;
		this.clientPass = clientPass;
		this.admin = admin;
		this.usertype = usertype;
		this.locationStatus = locationStatus;
		this.smsStatus = smsStatus;
		this.callStatus = callStatus;
		this.expiry = expiry;
		this.adminPhone = adminPhone;
		
	}
	
	public String getAdminPhone() {
		return adminPhone;
	}

	public void setAdminPhone(String adminPhone) {
		this.adminPhone = adminPhone;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getClientPass() {
		return clientPass;
	}

	public void setClientPass(String clientPass) {
		this.clientPass = clientPass;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public int getLocationInterval() {
		return locationInterval;
	}
	public void setLocationInterval(int locationInterval) {
		this.locationInterval = locationInterval;
	}
	public int getSmsInterval() {
		return smsInterval;
	}
	public void setSmsInterval(int smsInterval) {
		this.smsInterval = smsInterval;
	}
	public int getCallInterval() {
		return callInterval;
	}
	public void setCallInterval(int callInterval) {
		this.callInterval = callInterval;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	
	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	
	public int getLocationStatus() {
		return locationStatus;
	}

	public void setLocationStatus(int locationStatus) {
		this.locationStatus = locationStatus;
	}

	public int getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(int smsStatus) {
		this.smsStatus = smsStatus;
	}

	public int getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(int callStatus) {
		this.callStatus = callStatus;
	}


}
