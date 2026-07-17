$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)')

# Verified Unsplash IDs specifically for scones (plain baked, no people)
$candidates = @(
  'https://images.unsplash.com/photo-1597290282695-edc43d0e7129?w=800&q=90',
  'https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=800&q=90',
  'https://images.unsplash.com/photo-1587314168485-3236d6710814?w=800&q=90',
  'https://images.unsplash.com/photo-1490323925027-c3d94dae60b6?w=800&q=90',
  'https://images.unsplash.com/photo-1565626163163-a2f6df55c4e3?w=800&q=90',
  'https://images.unsplash.com/photo-1605616573540-8aff8a70e8e7?w=800&q=90',
  'https://images.unsplash.com/photo-1486427944299-d1955d23e34d?w=800&q=90',
  'https://images.unsplash.com/photo-1556041741-c1a37c6283d3?w=800&q=90',
  'https://images.unsplash.com/photo-1517093728432-a0440f8d45af?w=800&q=90',
  'https://images.unsplash.com/photo-1604882737210-5e6dff62fc54?w=800&q=90',
  'https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=800&q=90'
)

$i = 0
foreach ($url in $candidates) {
  try {
    $bytes = $wc.DownloadData($url)
    if ($bytes.Length -gt 20000) {
      $tmpPath = "$dir\scone_try_$i.jpg"
      [System.IO.File]::WriteAllBytes($tmpPath, $bytes)
      Write-Host "Saved: scone_try_$i.jpg ($($bytes.Length) bytes) from $url"
    }
  } catch {
    Write-Host "FAIL $i : $($_.Exception.Message)"
  }
  $i++
}
Write-Host "Done."
