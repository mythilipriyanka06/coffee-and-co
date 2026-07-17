$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)')

$urls = @(
  'https://source.unsplash.com/800x800/?english+scone+baked',
  'https://source.unsplash.com/800x800/?scone+cream+jam',
  'https://source.unsplash.com/800x800/?scone+bakery',
  'https://source.unsplash.com/800x800/?plain+scone+golden',
  'https://source.unsplash.com/800x800/?scone+british+tea'
)

$labels = @('scone-english','scone-cream','scone-bakery','scone-plain','scone-tea')

for ($i = 0; $i -lt $urls.Count; $i++) {
  $url   = $urls[$i]
  $label = $labels[$i]
  try {
    $bytes = $wc.DownloadData($url)
    if ($bytes.Length -gt 30000) {
      $path = "$dir\$label.jpg"
      [System.IO.File]::WriteAllBytes($path, $bytes)
      Write-Host "OK: $label $($bytes.Length) bytes"
    } else {
      Write-Host "SMALL: $label $($bytes.Length)"
    }
  } catch {
    Write-Host "FAIL: $label $($_.Exception.Message)"
  }
}
Write-Host 'Done'
