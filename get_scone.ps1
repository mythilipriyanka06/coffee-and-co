$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$tgt = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\target\classes\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36')

# Multiple specific Unsplash photo IDs for plain scones (no people, no hands)
# Each entry is a known Unsplash photo URL for bakery/scone product shots
$candidates = @(
  'https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1597290282695-edc43d0e7129?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1621997895729-64e0dd0c5d76?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1587314168485-3236d6710814?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1490323925027-c3d94dae60b6?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1565626163163-a2f6df55c4e3?w=800&q=90&fit=crop',
  'https://images.unsplash.com/photo-1605616573540-8aff8a70e8e7?w=800&q=90&fit=crop'
)

$downloaded = $false
foreach ($url in $candidates) {
  try {
    Write-Host "Trying: $url"
    $bytes = $wc.DownloadData($url)
    if ($bytes.Length -gt 30000) {
      # Save to temp first
      $tmpPath = "$dir\scone_new.jpg"
      [System.IO.File]::WriteAllBytes($tmpPath, $bytes)
      Write-Host "Downloaded $($bytes.Length) bytes - SAVED as scone_new.jpg"
      $downloaded = $true
      break
    } else {
      Write-Host "  Too small: $($bytes.Length) bytes, skipping"
    }
  } catch {
    Write-Host "  FAIL: $($_.Exception.Message)"
  }
}

if (-not $downloaded) {
  Write-Host "ALL candidates failed."
} else {
  Write-Host "SUCCESS - review scone_new.jpg before it replaces the old one"
}
