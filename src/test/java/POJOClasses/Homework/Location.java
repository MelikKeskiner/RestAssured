package POJOClasses.Homework;

public class Location {
    class Address {
        private String street;

        public String getStreet() {
            return street;
        }
    }

    class Company {
        private String name;

        public String getName() {
            return name;
        }
    }

    class User {
        private String email;
        private Address address;
        private String phone;
        private Company company;

        public String getEmail() {
            return email;
        }

        public Address getAddress() {
            return address;
        }

        public String getPhone() {
            return phone;
        }

        public Company getCompany() {
            return company;
        }
    }
}
