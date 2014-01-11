Set objWMIService = GetObject("winmgmts:\\.\root\cimv2")
Set colItems = objWMIService.ExecQuery _ 
   ("Select * from Win32_ComputerSystemProduct") 
For Each objItem in colItems 
    Wscript.Echo objItem.UUID 
    exit for  ' do the first cpu only! 
Next 
