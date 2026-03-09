<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management Form</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <style>
        body { padding: 20px; }
        .form-container { max-width: 500px; margin: 20px auto; padding: 30px; border: 1px solid #ddd; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .form-group label { font-weight: bold; }
    </style>
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h2 align="center">
                <c:if test="${user.id == 0 || user.id == null}">Add New User</c:if>
                <c:if test="${user.id != 0 && user.id != null}">Edit User</c:if>
            </h2>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    <strong>Error:</strong> ${errorMessage}
                </div>
            </c:if>

            <form action="update" method="post">
                <c:if test="${user.id != 0 && user.id != null}">
                    <input type="hidden" name="id" value="<c:out value='${user.id}' />">
                </c:if>
                <div class="form-group">
                    <label for="name">Name:</label>
                    <input type="text" class="form-control" id="name" name="name" value="${user.name}" required>
                </div>
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" class="form-control" id="email" name="email" value="${user.email}" required>
                </div>
                <div class="form-group">
                    <label for="phone">Phone:</label>
                    <input type="tel" class="form-control" id="phone" name="phone" value="${user.phone}" required>
                </div>
                <div class="form-group">
                    <label for="role">Role:</label>
                    <select class="form-control" id="role" name="role" required>
                        <option value="" disabled <c:if test="${empty user.role}">selected</c:if>>-- Select Role --</option>
                        <option value="Admin" <c:if test="${user.role eq 'Admin'}">selected</c:if>>Admin</option>
                        <option value="Student" <c:if test="${user.role eq 'Student'}">selected</c:if>>Student</option>
                        <option value="Instructor" <c:if test="${user.role eq 'Instructor'}">selected</c:if>>Instructor</option>
                    </select>
                </div>

                <div class="text-center">
                    <c:choose>
                        <c:when test="${user.id != 0 && user.id != null}">
                            <button type="submit" class="btn btn-primary">Update User</button>
                            <a href="list" class="btn btn-secondary">Cancel</a>
                        </c:when>
                        <c:otherwise>
                            <button type="submit" class="btn btn-primary">Add User</button>
                            <a href="list" class="btn btn-secondary">Cancel</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </form>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</body>
</html>