<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Java Techie Mail</title>
 <!-- Link to Google Fonts for Mulish -->
    <link href="https://fonts.googleapis.com/css2?family=Mulish:wght@400;700&display=swap" rel="stylesheet" type="text/css">
    <style>
        /* Apply Mulish font globally */
        body, table, td, div {
            font-family: 'Mulish', sans-serif;
        }
        .button {
        	padding:15px 20px; 
        	border-radius: 10px; 
        	background-color: #1474bd; 
        	border: 0; 
        	cursor: pointer;
        	text-decoration: none;
        }
        .title {
        	font-size: 40px; 
        	color:#1474bd;
        }
        .desc {
        	font-size: 16px;
        }
    </style>
</head>

<body>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align="center" valign="top" 
				style="background-image: linear-gradient(0deg,#dbf0fd,#20a0f0);"><br> <br>
				<table width="600" border="0" cellspacing="0" cellpadding="0">
					<tr>
				        <td align="center" valign="top" style="padding-bottom: 25px;">
							<a href=${baseUrl} target="_blank">
				            	<img src="cid:logoImage" alt="Logo" style="width: 120px; height: auto;" />
							</a>
				        </td>
				    </tr>
					<tr>
						<td align="left" valign="top" bgcolor="#ffff"
							style="background-color: #ffff; font-size: 13px; color: #000000; padding: 45px; border-radius: 10px;">
							<div class="title">
								<b>Forgot your password?</b>
							</div>
							<br><br>
							<div class="desc">Hey, we received a request to reset your password.</div>
							<br>
							<div class="desc">Let’s get you a new one!</div>
							<br><br>
							<a class="button" href=${resetUrl} target="_blank">
				            	<b style="color:#ffff">RESET MY PASSWORD</b>
				            </a>
							<br><br><br>
							<div class="desc">Didn’t request a password reset? You can ignore this message.<br/><br/>
							<i style="color: #808080">This link will expire in the next 30 minutes.</i></div>
							<br>
						</td>
						
					</tr>

				</table> <br> <br> <br></td>
		</tr>
	</table>
</body>
</html>