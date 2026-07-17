[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$w = New-Object System.Net.WebClient
$w.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36')
$res = $w.DownloadString('https://commons.wikimedia.org/w/api.php?action=query&titles=File:Scone_with_jam_and_cream.jpg|File:Pile_of_scones.jpg|File:Scones_with_jam_and_cream.jpg&prop=imageinfo&iiprop=url&format=json')
Write-Host $res
