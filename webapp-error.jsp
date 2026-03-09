<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Error Page</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <style>
        body { padding: 20px; text-align: center; }
        .error-container { margin-top: 50px; }
    </style>
</head>
<body>
    <div class="container error-container">
        <h1>Oops! Something went wrong.</h1>
        <p class="lead">
            <c:if test="${not empty errorMessage}">
                ${errorMessage}
            </c:if>
            <c:if test="${empty errorMessage}">
                An unexpected error occurred. Please try again later.
            </c:if>
        </p>
        <a href="list" class="btn btn-primary">Go to User List</a>
    </div>
</body>
</html>