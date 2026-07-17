$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$tgt = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\target\classes\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)')

# Confirmed Wikimedia Commons scone images
$urls = @(
  'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Baked_scones_serving.jpg/960px-Baked_scones_serving.jpg',
  'https://upload.wikimedia.org/wikipedia/commons/1/1c/Baked_scones_serving.jpg',
  'https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Scones_with_jam_and_cream.jpg/800px-Scones_with_jam_and_cream.jpg',
  'https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Scone_2009.jpg/800px-Scone_2009.jpg',
  'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8b/Chocolate_chip_scone.jpg/800px-Chocolate_chip_scone.jpg'
)

$labels = @('scone_wiki1','scone_wiki1b','scone_jam','scone_plain','scone_choc')

for ($i = 0; $i -lt $urls.Count; $i++) {
  try {
    $bytes = $wc.DownloadData($urls[$i])
    if ($bytes.Length -gt 20000) {
      $path = "$dir\$($labels[$i]).jpg"
      [System.IO.File]::WriteAllBytes($path, $bytes)
      Write-Host "OK: $($labels[$i]) $($bytes.Length)"
    }
  } catch {
    Write-Host "FAIL: $($labels[$i]) - $($_.Exception.Message)"
  }
}
Write-Host 'Done'
