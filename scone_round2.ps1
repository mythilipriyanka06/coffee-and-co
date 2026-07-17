$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)')

# Direct Unsplash CDN URLs for confirmed scone photos
# IDs from Unsplash search results for "scone"
$urls = @(
  'https://images.unsplash.com/photo-1582169296194-e4d644c48063?w=800&q=90',
  'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=800&q=90',
  'https://images.unsplash.com/photo-1513442542250-854d436a73f2?w=800&q=90',
  'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=800&q=90',
  'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=800&q=90',
  'https://images.unsplash.com/photo-1517093728432-a0440f8d45af?w=800&q=90',
  'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800&q=90',
  'https://images.unsplash.com/photo-1612182617117-e2b3e4c94a44?w=800&q=90',
  'https://images.unsplash.com/photo-1560780552-ba54683cb263?w=800&q=90',
  'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=800&q=90'
)

for ($i = 0; $i -lt $urls.Count; $i++) {
  $url = $urls[$i]
  try {
    $bytes = $wc.DownloadData($url)
    if ($bytes.Length -gt 20000) {
      $path = "$dir\scone_r$i.jpg"
      [System.IO.File]::WriteAllBytes($path, $bytes)
      Write-Host "OK: scone_r$i $($bytes.Length) bytes"
    } else {
      Write-Host "SMALL: idx $i"
    }
  } catch {
    Write-Host "FAIL: idx $i $($_.Exception.Message)"
  }
}
Write-Host 'Done'
