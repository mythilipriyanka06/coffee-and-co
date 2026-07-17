$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)')

# These are SPECIFIC confirmed Unsplash photo IDs from the search result for scones:
# L-I8vVz_J9o = cranberry scones under glass dome
# V6S9lT6E1sY = blueberry scones with white icing on cooling rack
# S9oX7f9G9_w = scones with jam and cream

$photos = @(
  @{id='L-I8vVz_J9o'; label='scone_cranberry'},
  @{id='V6S9lT6E1sY'; label='scone_blueberry'},
  @{id='S9oX7f9G9_w'; label='scone_jam_cream'},
  @{id='U3y8xL9p5_A'; label='scone_plain'}
)

foreach ($p in $photos) {
  $url = "https://images.unsplash.com/photo-$($p.id)?w=800&q=90"
  try {
    $bytes = $wc.DownloadData($url)
    if ($bytes.Length -gt 20000) {
      $path = "$dir\$($p.label).jpg"
      [System.IO.File]::WriteAllBytes($path, $bytes)
      Write-Host "OK: $($p.label) $($bytes.Length) bytes"
    } else {
      Write-Host "SMALL: $($p.label)"
    }
  } catch {
    Write-Host "FAIL: $($p.label) $($_.Exception.Message)"
  }
}
Write-Host 'Done'
