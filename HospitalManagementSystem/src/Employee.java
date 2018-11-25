
public class Employee {
	private int id;
	private String name;
	private int age;
	private long phoneNumber;
	private String office;
	private String specialty;
	
	public Employee(int id, String name, int age, long phoneNumber, String office, String specialty) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.phoneNumber = phoneNumber;
		this.office = office;
		this.specialty = specialty;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public long getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpeciality(String specialty) {
		this.specialty = specialty;
	}
}
