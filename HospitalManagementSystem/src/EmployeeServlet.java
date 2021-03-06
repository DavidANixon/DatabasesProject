import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

public class EmployeeServlet extends HttpServlet {
	EmployeeDAO edao;

	public void init() throws ServletException {
		edao = new EmployeeDAO();
	}
	
	//Return a list of all employees
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Setting up the content type of webpage
		response.setContentType("text/html");
		// Writing message to the web page
		PrintWriter out = response.getWriter();
		
		EmployeeDAO edao = new EmployeeDAO();
		String id = request.getParameter("id");
		if(id.equals("-1")) {
			List<Employee> lst = new ArrayList<Employee>();
			try {
				out.println("<h1>" + "List all Employees" + "</h1>");
				//Get all initial data
				lst = edao.getAllEmployees();
				if(lst != null) {
					out.println("<p> <strong> ID | Name        | Age | Phone     | Office    | Specialty </strong></p>");
					for(int i = 0; i < lst.size(); i++) {
						Employee tempE = lst.get(i);
						out.println("<p>" + tempE.getId() + " | " +  tempE.getName() + " | " + tempE.getAge() + " | " + tempE.getPhone() +" | " + tempE.getOffice() + " | " + tempE.getSpecialty() + "</p>");
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
					edao.deleteEmployee(Integer.parseInt(id));
					out.println("<h1> Employee Deleted</h1>");
				}catch (Exception e) {
					
				}
			}
		}
	}
	
	//Add a new employee
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // read form fields
		String id = request.getParameter("id");
        String name = request.getParameter("name");
        String age = request.getParameter("age");
        String phone = request.getParameter("phone");
        String office = request.getParameter("office");
        String specialty = request.getParameter("specialty");
        
        EmployeeDAO edao = new EmployeeDAO();
        if(id.equals("-1")) {
        	try {
    			Employee e = new Employee(3, name, Integer.parseInt(age), phone, office, specialty);
    			edao.addEmployee(e);
    			
    			// get response writer
    	        PrintWriter writer = response.getWriter();
    	        // build HTML code
    	        String htmlRespone = "<html>";
    	        htmlRespone += "<h2> Employee Added. </br></h2>";
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
        }else {
        	try {
    			Employee e = new Employee(Integer.parseInt(id), name, Integer.parseInt(age), phone, office, specialty);
    			edao.updateEmployee(e);
    			 // get response writer
    	        PrintWriter writer = response.getWriter();
    	        // build HTML code
    	        String htmlRespone = "<html>";
    	        htmlRespone += "<h2> Employee Updated. </br></h2>";
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
        
       
        //TODO: Add a back button so the user can return to the previous page        
    }
	
}