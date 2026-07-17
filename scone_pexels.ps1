$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)')
$wc.Headers.Add('Referer', 'https://www.pexels.com/')

# Pexels free images - direct CDN links for scones and baked goods
# These are known working Pexels photo CDN URLs for scone/bakery items
$candidates = @(
  'https://images.pexels.com/photos/1775043/pexels-photo-1775043.jpeg?w=800',
  'https://images.pexels.com/photos/461428/pexels-photo-461428.jpeg?w=800',
  'https://images.pexels.com/photos/1775043/pexels-photo-1775043.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/205961/pexels-photo-205961.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/1660030/pexels-photo-1660030.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/1721934/pexels-photo-1721934.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/1099680/pexels-photo-1099680.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/3766177/pexels-photo-3766177.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/6546433/pexels-photo-6546433.jpeg?auto=compress&cs=tinysrgb&w=800',
  'https://images.pexels.com/photos/2674062/pexels-photo-2674062.jpeg?auto=compress&cs=tinysrgb&w=800'
)

for ($i = 0; $i -lt $candidates.Count; $i++) {
  $url = $candidates[$i]
  try {
    $bytes = $wc.DownloadData($url)
    if ($bytes.Length -gt 20000) {
      $path = "$dir\scone_pex$i.jpg"
      [System.IO.File]::WriteAllBytes($path, $bytes)
      Write-Host "OK scone_pex$i $($bytes.Length)"
    } else {
      Write-Host "SMALL $i $($bytes.Length)"
    }
  } catch {
    Write-Host "FAIL $i $($_.Exception.Message)"
  }
}
Write-Host 'Done'
