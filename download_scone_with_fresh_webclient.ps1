[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$candidates = @(
  @{url='https://upload.wikimedia.org/wikipedia/commons/9/9c/Plain_filled_scone_jam_first.jpg'; label='scone_jam_first'},
  @{url='https://upload.wikimedia.org/wikipedia/commons/0/09/Scone_with_clotted_cream_and_jam_-_Dyke_Road_Park_Cafe_2025-08-10.jpg'; label='scone_dyke_road'},
  @{url='https://upload.wikimedia.org/wikipedia/commons/6/63/Cream_Scones_2_-_after_baking.jpg'; label='scone_after_baking'},
  @{url='https://upload.wikimedia.org/wikipedia/commons/b/bc/Buttermilk-Scones-batch.jpg'; label='scone_buttermilk'},
  @{url='https://upload.wikimedia.org/wikipedia/commons/6/6f/Scone_with_jam_and_cream.jpg'; label='scone_wiki_jam'}
)

$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'

foreach ($c in $candidates) {
  try {
    # Re-instantiate WebClient to ensure headers are fresh and present
    $w = New-Object System.Net.WebClient
    $w.Headers.Add('User-Agent', 'CoffeeCoCafeMenuManager/1.0 (https://localhost:8080/; support@coffeeco.com)')
    
    $bytes = $w.DownloadData($c.url)
    if ($bytes.Length -gt 10000) {
      [System.IO.File]::WriteAllBytes("$dir\$($c.label).jpg", $bytes)
      Write-Host "Downloaded $($c.label).jpg: $($bytes.Length) bytes"
    }
  } catch {
    Write-Host "FAIL $($c.label): $_"
  }
}
