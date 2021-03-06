import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppointmentServlet extends HttpServlet {
	AppointmentDAO adao;
	EmployeeDAO edao;
	PatientDAO pdao;
	OperatingRoomDAO ordao;
	OperationDAO odao;

	public void init() throws ServletException {
		adao = new AppointmentDAO();
		edao = new EmployeeDAO();
		pdao = new PatientDAO();
		ordao = new OperatingRoomDAO();
		odao = new OperationDAO();
		
	}
	//Return a list of all Appointments
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Setting up the content type of webpage
		response.setContentType("text/html");
		// Writing message to the web page
		PrintWriter out = response.getWriter();
		out.println("<h1>" + "List all Appointments" + "</h1>");
		String id = request.getParameter("id");
		if(id.equals("-1")) {
			List<Appointment> lst = new ArrayList<Appointment>();
			try {
				//Get all initial data
				lst = adao.getAllAppointments();
				if(lst != null) {
					//| Appointment_ID | Date       | Time | OR_ID | Patient_ID | Operation_ID | Doctor_ID | Nurse_ID |
					out.println("<p> <strong> ID |___Date__| Time | Operating Room | Patient |____Operation____| Doctor | Nurse </strong></p>");
					for(int i = 0; i < lst.size(); i++) {
						Appointment tempP = lst.get(i);
						String operation = odao.getOperation(tempP.getOperation_id()).getName();
						String doctorName = edao.getEmployee(tempP.getDoctor_id()).getName();
						String nurseName = edao.getEmployee(tempP.getNurse_id()).getName();
						String oproomBld = ordao.getOperatingRoom(tempP.getOr_id()).getBuilding();
						int oproomRm = ordao.getOperatingRoom(tempP.getOr_id()).getRoomNumber();
						String patientName = pdao.getPatient(tempP.getPatient_id()).getName();
						
						out.println("<p>" + tempP.getId() + " | " 
										  +  tempP.getDate() + " | " 
										  + tempP.getTime() + ":00 | " 
										  + oproomBld + " " + oproomRm + " | " 
										  + patientName + " | " 
										  + operation + " | " 
										  + doctorName + " | " 
										  + nurseName +  "</p>");
					}
				}else {
					out.println("<p>" + "There is no data in the table or a table was not found." + "<p>");
				}
			} catch (Exception e) {
				
			}
		}else {
			if(id.equals("")) {
				out.println("<p> Input not valid</p>");
			}else {
				try {
					adao.deleteAppointment(Integer.parseInt(id));
					out.println("<p> Appointment Deleted</p>");
				}catch (Exception e) {
					
				}
			}
		}
	}
	
	private String orConflict(int id) {
		if (id != -1) {
			return "<p>The selected Operating Room has a time conflict with the appointment associated with ID:#" + id + ".</p>";
		}
		return "";
	}
	private String drConflict(int id) {
		if (id != -1) {
			return "<p>The selected Doctor has a time conflict with the appointment associated with ID:#" + id + ".</p>";
		}
		return "";
	}
	private String nurseConflict(int id) {
		if (id != -1) {
			return "<p>The selected Nurse has a time conflict with the appointment associated with ID:#" + id + ".</p>";
		}
		return "";
	}
	
	private boolean conflictsExist(String dateStr, String time, String orId, String oId, String dId, String nId) {
		try {
			
			int duration = operationDuration(edao.getEmployee(Integer.parseInt(dId)),odao.getOperation(Integer.parseInt(oId)));
			int orOk = adao.checkForORTimeConflicts(dateStr, Integer.parseInt(time), duration, Integer.parseInt(orId));
			int drOk = adao.checkForDoctorTimeConflicts(dateStr, Integer.parseInt(time), duration, Integer.parseInt(dId));
			int nurseOk = adao.checkForNurseTimeConflicts(dateStr, Integer.parseInt(time), duration, Integer.parseInt(nId));
			
			return !(orOk == -1 && drOk ==-1 && nurseOk ==-1);
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}	
	}
	
	//Add a new Appointment
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // read form fields
	
		String id = request.getParameter("id");
		String dateStr = request.getParameter("Date");
        String time = request.getParameter("Time");
        String orId = request.getParameter("OR_id");
        String pId = request.getParameter("P_id");
        String oId = request.getParameter("O_id");
        String dId = request.getParameter("D_id");
        String nId = request.getParameter("N_id");
        
        String buttonResult;
        if (request.getParameter("add_button") != null) 
        	buttonResult = "add";
        else if (request.getParameter("check_button") != null)
        	buttonResult = "check";
        else
        	buttonResult = "notAddOrCheck";

        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		// If check appointment is selected
		if (buttonResult.equals("check")) {
        	try {
        		// get response writer
    	        PrintWriter writer = response.getWriter();
    	        
        		if (dataTypesValid(time, orId, pId, oId, dId, nId)) {       
        			
        			int duration = operationDuration(edao.getEmployee(Integer.parseInt(dId)),odao.getOperation(Integer.parseInt(oId)));
        			int orOk = adao.checkForORTimeConflicts(dateStr, Integer.parseInt(time), duration, Integer.parseInt(orId));
        			int drOk = adao.checkForDoctorTimeConflicts(dateStr, Integer.parseInt(time), duration, Integer.parseInt(dId));
       			    int nurseOk = adao.checkForNurseTimeConflicts(dateStr, Integer.parseInt(time), duration, Integer.parseInt(nId));  
	
        			String htmlResponse = "";
        			// there were no conflicts
        			if (!conflictsExist(dateStr, time, orId, oId, dId, nId)) {
        				htmlResponse = "<h1> Appointment Available! </h1>";
    	    	        htmlResponse += "<p>The selected appointment is avaiable, return to the Appointments page to schedule it. </p>";
    	    	        htmlResponse += "<button onclick=\"goBack()\">Return to Appointments</button>\n" + 
    	    	        		"\n" + 
    	    	        		"<script>\n" + 
    	    	        		"function goBack() {\n" + 
    	    	        		"    window.history.back();\n" + 
    	    	        		"}\n" + 
    	    	        		"</script>";
    	    	        htmlResponse += "</html>";
        			}
        			//there were conflicts
        			else {
        				htmlResponse = "<h1> Appointment Not Available </h1>";
    	    	        htmlResponse += "<p>The appointment selected has a time conflict with one or more already scheduled appointments. See details below.</p>";
    	    	        htmlResponse += orConflict(orOk);
    	    	        htmlResponse += drConflict(drOk);
    	    	        htmlResponse += nurseConflict(nurseOk);
    	    	        htmlResponse += "<button onclick=\"goBack()\">Return to Appointments</button>\n" + 
    	    	        		"\n" + 
    	    	        		"<script>\n" + 
    	    	        		"function goBack() {\n" + 
    	    	        		"    window.history.back();\n" + 
    	    	        		"}\n" + 
    	    	        		"</script>";
    	    	        htmlResponse += "</html>";
        			}
        			
        			writer.println(htmlResponse);
        			}
      	
       
	        	else {
	    			String htmlResponse = "<html>";
	    	        htmlResponse = "<h1> Input Error </h1>";
	    	        htmlResponse += "<p>The appointment contains an input error. </br> Make sure your employees have the correct specialties and that all fields are filled with integers.</p>";
	    	        htmlResponse += "<button onclick=\"goBack()\">Return to Appointments</button>\n" + 
	    	        		"\n" + 
	    	        		"<script>\n" + 
	    	        		"function goBack() {\n" + 
	    	        		"    window.history.back();\n" + 
	    	        		"}\n" + 
	    	        		"</script>";
	    	        htmlResponse += "</html>";
	    	        writer.println(htmlResponse);
	        	}
        	
        	} catch (Exception e) {
        			e.printStackTrace();
        	}
        }

		//If Add appointment is selected
		else if(buttonResult.equals("add")) {
        	try {   		
    			
    			// get response writer
    	        PrintWriter writer = response.getWriter();
        		
        		if (dataTypesValid(time, orId, pId, oId, dId, nId) && !conflictsExist(dateStr, time, orId, oId, dId, nId)) {  
        			
        		
	    			Appointment p = new Appointment(3, date, Integer.parseInt(time), Integer.parseInt(orId), Integer.parseInt(pId), Integer.parseInt(oId), Integer.parseInt(dId), Integer.parseInt(nId));
	    			adao.addAppointment(p);
	   
	    			
	    			//Get data for response message
	    			Employee dr = edao.getEmployee(Integer.parseInt(dId));
	    			Employee nurse = edao.getEmployee(Integer.parseInt(nId));
	    			Operation op = odao.getOperation(Integer.parseInt(oId));
	    			OperatingRoom oprm = ordao.getOperatingRoom(Integer.parseInt(orId));
	    			Patient pt = pdao.getPatient(Integer.parseInt(pId));
	    	        
	    	        // build HTML code
	    	        String htmlResponse = "<html>";
	    	        htmlResponse = "<h1> Success! </h1>";
	    	        htmlResponse += "<p>The appointment for " + pt.getName() 
	    	        								 + " on " + date 
	    	        								 + " with Dr. " + dr.getName()
	    	        								 + " and Nurse " + nurse.getName()
	    	        						
	    	        								 + " in " + oprm.getBuilding() + " " + oprm.getRoomNumber()
	    	        								 + " has been scheduled. </br></p>";
	    	        htmlResponse += "<button onclick=\"goBack()\">Finish</button>\n" + 
	    	        		"\n" + 
	    	        		"<script>\n" + 
	    	        		"function goBack() {\n" + 
	    	        		"    window.history.back();\n" + 
	    	        		"}\n" + 
	    	        		"</script>";
	    	        htmlResponse += "</html>";
	    	        writer.println(htmlResponse);
        		}
        		else {	    	
              	
        			String htmlResponse = "<html>";
	    	        htmlResponse = "<h1> Input Error </h1>";
	    	        htmlResponse += "<p>The appointment contains an input error. </br> Be sure to check that your appointment has no time conflicts before attempting to add it. </br>";
	    	        htmlResponse += "Also make sure your employees have the correct specialties and that all fields are filled with integers.</p>";
	    	        htmlResponse += "<button onclick=\"goBack()\">Return to Appointments</button>\n" + 
	    	        		"\n" + 
	    	        		"<script>\n" + 
	    	        		"function goBack() {\n" + 
	    	        		"    window.history.back();\n" + 
	    	        		"}\n" + 
	    	        		"</script>";
	    	        htmlResponse += "</html>";
	    	        writer.println(htmlResponse);
        		}
        			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        } 
        else {
        	try {
    			Appointment p = new Appointment(Integer.parseInt(id), date, Integer.parseInt(time), Integer.parseInt(orId), Integer.parseInt(pId), Integer.parseInt(oId), Integer.parseInt(dId),Integer.parseInt(nId));
    			adao.updateAppointment(p);
    			 // get response writer
    	        PrintWriter writer = response.getWriter();
    	        // build HTML code
    	        String htmlRespone = "<html>";
    	        htmlRespone += "<h2> Appointment Updated. </br></h2>";
    	        htmlRespone += "<button onclick=\"goBack()\">Finish</button>\n" + 
    	        		"\n" + 
    	        		"<script>\n" + 
    	        		"function goBack() {\n" + 
    	        		"    window.history.back();\n" + 
    	        		"}\n" + 
    	        		"</script>";
    	        htmlRespone += "</html>";
    	        writer.println(htmlRespone);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
    }
	
	/////////////// HELPER AND VALIDATION METHODS ///////////////////////////////////////
	
	private int operationDuration(Employee dr, Operation op) {
		try {
			for(Operation operation : odao.getAllOperations()) {				
				if (operation.getName().equals(op.getName()) && dr.getSpecialty().equals(operation.getSpecialty()))
					return operation.getDuration();
			}
			return -1;
		}
		catch (Exception e) {
 			return -1;
		}
	}
	
	private boolean stringsNonEmpty(String time, String orId, String pId, String oId, String dId, String nId) {
		List<String> paramList = new ArrayList<>(Arrays.asList(time, orId, pId, oId, dId, nId));
		for(String param : paramList) {
			if (param.isEmpty())
				return false;
		}
		return true;
	}
	
	private boolean drIsQualified(Employee dr, Operation op) {
		try {
			for(Operation operation : odao.getAllOperations()) {				
				if (operation.getName().equals(op.getName()) && dr.getSpecialty().equals(operation.getSpecialty()))
					return true;
			}
			return false;
		}
		catch (Exception e) {
 			return false;
		}
	}
	
	private boolean operationIsTreatment(Patient pt, Operation op) {
		try {
			for(Operation operation : odao.getAllOperations()) {
				if (operation.getName().equals(op.getName()) && pt.getIllness().equals(operation.getIllness())) 
					return true;
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	private boolean specialtiesMatch(String pId, String oId, String dId, String nId) {
		try {
			Employee dr = edao.getEmployee(Integer.parseInt(dId));
			Employee nurse = edao.getEmployee(Integer.parseInt(nId));
			Operation op = odao.getOperation(Integer.parseInt(oId));
			Patient pt = pdao.getPatient(Integer.parseInt(pId));
			
			if(!nurse.getSpecialty().equals("Nursing"))
				return false;
			
			if(!drIsQualified(dr, op))
				return false;
			
			if(!operationIsTreatment(pt, op))
				return false;
			
			return true;
		}
		catch (Exception e) {
			return false;
		}
		
		
		
	}
	
	private boolean dataTypesValid(String time, String orId, String pId, String oId, String dId, String nId) {
		return (stringsNonEmpty(time, orId, pId, oId, dId, nId) && specialtiesMatch(pId, oId, dId, nId));
	}
}
