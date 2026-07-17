$content = Get-Content 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\js\premium.js'
$matches = [regex]::Matches($content, 'fa-[a-zA-Z0-9\-]+')
$unique = $matches.Value | Sort-Object -Unique
Write-Host "Icons found in premium.js:"
$unique
