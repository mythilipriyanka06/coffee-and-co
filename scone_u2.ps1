$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)')

# These Unsplash IDs come from specific food photography searches
# Using the photo-XXXXXXXX format that Unsplash actually uses
$candidates = @(
  'https://images.unsplash.com/photo-1550617931-e17a7b70dce2?w=800&q=90',
  'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=800&q=90',
  'https://images.unsplash.com/photo-1559622214-f8a9850965bb?w=800&q=90',
  'https://images.unsplash.com/photo-1481769961499-c51678cafa9b?w=800&q=90',
  'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=800&q=90',
  'https://images.unsplash.com/photo-1506459225024-1428097a7e18?w=800&q=90',
  'https://images.unsplash.com/photo-1519915028121-7d3463d20b13?w=800&q=90',
  'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=800&q=90',
  'https://images.unsplash.com/photo-1510915228340-29c85a43dcfe?w=800&q=90',
  'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&q=90'
)

for ($i = 0; $i -lt $candidates.Count; $i++) {
  $url = $candidates[$i]
  try {
    $bytes = $wc.DownloadData($url)
    if ($bytes.Length -gt 20000) {
      $path = "$dir\scone_u2_$i.jpg"
      [System.IO.File]::WriteAllBytes($path, $bytes)
      Write-Host "OK scone_u2_$i $($bytes.Length)"
    }
  } catch {
    Write-Host "FAIL $i"
  }
}
Write-Host 'Done'
