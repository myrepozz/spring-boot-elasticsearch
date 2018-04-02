function validatePrice(formName)
{
	var value=document.forms[formName]["price"].value;
    if (isNaN(value))
    {
    	document.forms[formName]["price"].value= 0;
    }
}