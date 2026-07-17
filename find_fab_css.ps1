$content = Get-FileHash -Path 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\css\premium.css'
# Wait, let's write a script to find all css classes containing 'fab' or ending with '-fab' or related to the floating buttons.
$css = Get-Content 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\css\premium.css'
$matches = [regex]::Matches($css, '\.[a-zA-Z0-9\-_]+')
$unique = $matches.Value | Sort-Object -Unique
$unique | Where-Object { $_ -like "*fab*" -or $_ -like "*widget*" }
