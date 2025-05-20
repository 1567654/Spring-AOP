package se.yrgo.dataaccess;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import se.yrgo.domain.Call;
import se.yrgo.domain.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

class CustomerMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        String customerId = rs.getString(1);
        String companyName = rs.getString(2);
        String email = rs.getString(3);
        String telephone = rs.getString(4);
        String notes = rs.getString(5);
        return new Customer(customerId, companyName, email, telephone, notes);
    }
}

class CallMapper implements RowMapper<Call> {

    @Override
    public Call mapRow(ResultSet rs, int rowNum) throws SQLException {
        Date timeAndDate = rs.getDate(2);
        String notes = rs.getString(3);
        return new Call(notes, timeAndDate);
    }
}


public class CustomerDaoJdbcTemplateImpl implements CustomerDao {
    private static final String DELETE_SQL = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID=?";
    private static final String UPDATE_SQL = "UPDATE CUSTOMER SET COMPANY_NAME=?, EMAIL=?, TELEPHONE=?, NOTES=? WHERE CUSTOMER_ID=?";
    private static final String INSERT_SQL = "INSERT INTO CUSTOMER (CUSTOMER_ID, COMPANY_NAME, EMAIL, TELEPHONE, NOTES) VALUES (?,?,?,?,?)";
    private static final String CREATE_TABLE_CUSTOMER_SQL = "CREATE TABLE CUSTOMER(CUSTOMER_ID VARCHAR(20), COMPANY_NAME VARCHAR(50), EMAIL VARCHAR(50), TELEPHONE VARCHAR(50), NOTES VARCHAR(50))";
    private static final String CREATE_TABLE_CALL_SQL = "CREATE TABLE TBL_CALL(ID INT, TIME_AND_DATE DATE, NOTES VARCHAR(50)";
    private JdbcTemplate template;

    public CustomerDaoJdbcTemplateImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void create(Customer customer) {
        template.update(INSERT_SQL, customer.getCustomerId(), customer.getCompanyName(), customer.getEmail(), customer.getTelephone(), customer.getNotes());
    }

    @Override
    public Customer getById(String customerId) throws RecordNotFoundException {
        return template.queryForObject("SELECT * FROM CUSTOMER WHERE CUSTOMER_ID=?", new CustomerMapper(), customerId);
    }

    @Override
    public List<Customer> getByName(String name) {
        return template.query("SELECT * FROM CUSTOMER WHERE COMPANY_NAME=?", new CustomerMapper(), name);
    }

    @Override
    public void update(Customer customerToUpdate) throws RecordNotFoundException {
        template.update(UPDATE_SQL, customerToUpdate.getCompanyName(), customerToUpdate.getEmail(),
                customerToUpdate.getTelephone(), customerToUpdate.getNotes(), customerToUpdate.getCustomerId());
    }

    @Override
    public void delete(Customer oldCustomer) throws RecordNotFoundException {
        template.update(DELETE_SQL, oldCustomer.getCustomerId());
    }

    @Override
    public List<Customer> getAllCustomers() {
        return template.query("SELECT * FROM CUSTOMER", new CustomerMapper());
    }

    @Override
    public Customer getFullCustomerDetail(String customerId) throws RecordNotFoundException {
        Customer customer = getById(customerId);
        List<Call> allCalls = template.query("SELECT * FROM TBL_CALL WHERE CUSTOMER_ID=?",
                new CallMapper(), customerId);
        customer.setCalls(allCalls);
        return customer;
    }

    @Override
    public void addCall(Call newCall, String customerId) throws RecordNotFoundException {
        Customer customer = getById(customerId);
        customer.addCall(newCall);
        template.update(UPDATE_SQL, customer.getCompanyName(), customer.getEmail(), customer.getTelephone(), customer.getNotes());
    }

    private void createTables(){
        try {
            template.update(CREATE_TABLE_CUSTOMER_SQL);
            template.update(CREATE_TABLE_CALL_SQL);
        } catch(Exception e) {
        }
    }
}
