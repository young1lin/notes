# 132

原文

Email.setHtml("<font color="red">\<hi\></font>" + message + "\<h1/>");

应该是

Email.setHtml("<font color="red">\<h1\></font>" + message + "\<h1/\>");

