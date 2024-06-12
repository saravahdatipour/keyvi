<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>
<!DOCTYPE html>
<html>
<head>
    <title>Yivi Login Error</title>
    <style>
        body { font-family: Arial, sans-serif; }
        .error { color: red; margin: 15px; }
    </style>
</head>
<body>
    <h1>Login Error</h1>
    <#if attributes.errors??>
        <div class="error">
            <h3>Following errors occurred during login:</h3>
            <ul>
                <#list attributes.errors as error>
                    <li>${error}</li>
                </#list>
            </ul>
        </div>
    <#else>
        <p>An unexpected error occurred. Please try again.</p>
    </#if>
    <!-- Provide a retry button that submits to the login action URL with a safe fallback -->
    <form method="post" action="${url.loginAction!'/auth'}">
        <button type="submit">Try Again</button>
    </form>
</body>
</html>
