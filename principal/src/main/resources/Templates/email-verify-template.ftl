<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Email Verification</title>
    <!-- Link to Google Fonts for Mulish -->
    <link href="https://fonts.googleapis.com/css2?family=Mulish:wght@400;700&display=swap" rel="stylesheet" type="text/css">
    <style>
        /* Apply Mulish font globally */
        body, table, td, div {
            font-family: 'Mulish', sans-serif;
        }
        .button {
            display: inline-block;
            padding: 10px; /* Keep padding as desired */
            border-radius: 20px; /* Rounded corners with 20px radius */
            background-color: #1474bd;
            color: #ffffff;
            text-decoration: none;
            font-weight: bold;
            font-size: 12px; /* Keep button text smaller */
            transition: background-color 0.3s ease, box-shadow 0.3s ease;
            width: 100%; /* Set the button to take the full width of the container */
            max-width: 200px; /* Optional: limit the maximum width of the button */
            text-align: center; /* Center text within the button */
            letter-spacing: 3px; /* Add spacing between letters */
        }
        .button:hover {
            background-color: #105b8d; 
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); 
        }
        .title {
            font-size: 40px; 
            color: #1474bd;
        }
        .desc {
            font-size: 16px;
        }
        .logo-section {
            background-image: linear-gradient(0deg, #dbf0fd, #20a0f0); /* Gradient background */
            padding: 20px; /* Add some padding */
            border-radius: 10px 10px 0 0; /* Rounded top corners */
        }
    </style>
</head>

<body>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="top"><br> <br>
                <table width="1000" border="0" cellspacing="0" cellpadding="0"> <!-- Increased width to 1000px -->
                    <tr>
                        <td class="logo-section" align="left" valign="top" style="padding-bottom: 25px;">
                            <a href="${baseUrl}" target="_blank">
                                <img src="cid:logoImage" alt="Logo" style="width: 120px; height: auto;" />
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td align="left" valign="top" bgcolor="#ffff"
                            style="background-color: #ffff; font-size: 13px; color: #000000; padding: 45px; border-radius: 0 0 10px 10px;">
                            <div class="title">
                                <b>Verify Your Email Address</b>
                            </div>
                            <br><br>
                            <div class="desc">Hey, we need to verify your email address.</div>
                            <br>
                            <div class="desc">Please click the button below to confirm your email address.</div>
                            <br><br>
                            <a class="button" href="${verificationUrl}" target="_blank">
                                <b style="color:#ffff">VERIFY MY EMAIL</b>
                            </a>
                            <br><br><br>
                            <div class="desc">Didn’t request this verification? You can ignore this message.<br/><br/>
                            <i style="color: #808080">This link is active for <span style="color: #d9534f;">15 minutes and can be used only once</span>. After that, it will expire and cannot be used again. Please take a moment to verify your email as soon as possible.</i></div>
                            <br>
                        </td>
                    </tr>
                </table> <br> <br> <br></td>
        </tr>
    </table>
</body>
</html>
