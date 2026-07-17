[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$w = New-Object System.Net.WebClient
$w.Headers.Add('User-Agent', 'CoffeeCoCafeMenuManager/1.0 (https://localhost:8080/; support@coffeeco.com)')

try {
    # Get category members of Category:Scones
    $catJson = $w.DownloadString('https://commons.wikimedia.org/w/api.php?action=query&list=categorymembers&cmtitle=Category:Scones&cmlimit=100&cmtype=file&format=json')
    [System.IO.File]::WriteAllText('scone_cat_members.json', $catJson)
    Write-Host "Success, saved to scone_cat_members.json"
} catch {
    Write-Host "FAIL: $_"
}
