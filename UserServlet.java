// src/main/java/com/example/servlet/UserServlet.java
package com.example.servlet;

import com.example.dao.UserDAO;
import com.example.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/") // Maps to the root URL and other unmapped requests
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath(); // Get the path after the context root

        try {
            switch (action) {
                case "/new":
                    showNewForm(request, response);
                    break;
                case "/insert":
                    insertUser(request, response);
                    break;
                case "/delete":
                    deleteUser(request, response);
                    break;
                case "/edit":
                    showEditForm(request, response);
                    break;
                case "/update":
                    updateUser(request, response);
                    break;
                case "/list": // Explicitly handle /list, or remove if you want '/' to list
                default:
                    listUser(request, response);
                    break;
            }
        } catch (SQLException ex) {
            // Log the exception and show an error page
            request.setAttribute("error", "An error occurred: " + ex.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            ex.printStackTrace(); // For development
        }
    }

    private void listUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        List<User> listUser = userDAO.selectAllUsers();
        request.setAttribute("listUser", listUser);
        request.getRequestDispatcher("user-list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("user-form.jsp").forward(request, response);
    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");

        // --- Input Validation ---
        boolean isValid = true;
        StringBuilder errorMessages = new StringBuilder();

        // 1. Name validation
        if (name == null || name.trim().isEmpty()) {
            errorMessages.append("Name cannot be empty. ");
            isValid = false;
        }

        // 2. Email validation (format and uniqueness)
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (email == null || email.trim().isEmpty() || !matcher.matches()) {
            errorMessages.append("Please enter a valid email address. ");
            isValid = false;
        } else if (!userDAO.isEmailUnique(email)) {
            errorMessages.append("Email address already exists. ");
            isValid = false;
        }

        // 3. Phone number validation (simple example, can be more complex)
        // Regex for digits and optional hyphens/spaces. Adjust as needed.
        String phoneRegex = "^\\+?[0-9\\s-]{10,}$";
        if (phone == null || phone.trim().isEmpty() || !phone.matches(phoneRegex)) {
            errorMessages.append("Please enter a valid phone number (at least 10 digits). ");
            isValid = false;
        }

        // 4. Role validation
        if (role == null || (!role.equals("Admin") && !role.equals("Student") && !role.equals("Instructor"))) {
            errorMessages.append("Please select a valid role. ");
            isValid = false;
        }
        // --- End Validation ---

        if (isValid) {
            User newUser = new User(name, email, phone, role);
            userDAO.insertUser(newUser);
            response.sendRedirect("list"); // Redirect to the list page after successful insertion
        } else {
            request.setAttribute("errorMessage", errorMessages.toString());
            // Forward back to the form with error messages and pre-filled data
            request.setAttribute("user", new User(name, email, phone, role)); // To pre-fill the form
            request.getRequestDispatcher("user-form.jsp").forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User existingUser = userDAO.selectUser(id);
        if (existingUser == null) {
            // Handle case where user is not found
            request.setAttribute("errorMessage", "User not found.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        request.setAttribute("user", existingUser);
        request.getRequestDispatcher("user-form.jsp").forward(request, response);
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");

        // --- Input Validation (similar to insert, but need to consider existing email for update) ---
        boolean isValid = true;
        StringBuilder errorMessages = new StringBuilder();

        if (name == null || name.trim().isEmpty()) {
            errorMessages.append("Name cannot be empty. ");
            isValid = false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (email == null || email.trim().isEmpty() || !matcher.matches()) {
            errorMessages.append("Please enter a valid email address. ");
            isValid = false;
        } else {
            // Check if email is unique, EXCLUDING the current user's email
            User currentUser = userDAO.selectUser(id);
            if (!email.equals(currentUser.getEmail()) && !userDAO.isEmailUnique(email)) {
                errorMessages.append("Email address already exists. ");
                isValid = false;
            }
        }

        String phoneRegex = "^\\+?[0-9\\s-]{10,}$";
        if (phone == null || phone.trim().isEmpty() || !phone.matches(phoneRegex)) {
            errorMessages.append("Please enter a valid phone number (at least 10 digits). ");
            isValid = false;
        }

        if (role == null || (!role.equals("Admin") && !role.equals("Student") && !role.equals("Instructor"))) {
            errorMessages.append("Please select a valid role. ");
            isValid = false;
        }
        // --- End Validation ---

        if (isValid) {
            User user = new User(id, name, email, phone, role);
            userDAO.updateUser(user);
            response.sendRedirect("list");
        } else {
            request.setAttribute("errorMessage", errorMessages.toString());
            request.setAttribute("user", new User(id, name, email, phone, role)); // To pre-fill the form with updated values
            request.getRequestDispatcher("user-form.jsp").forward(request, response);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        userDAO.deleteUser(id);
        response.sendRedirect("list");
    }
}