$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$wc  = New-Object System.Net.WebClient
$wc.Headers.Add('User-Agent', 'Mozilla/5.0')

function TryDownload($name, $urls) {
  foreach ($url in $urls) {
    try {
      $bytes = $wc.DownloadData($url)
      if ($bytes.Length -gt 10000) {
        [System.IO.File]::WriteAllBytes("$dir\$name.jpg", $bytes)
        Write-Host "OK $name ($($bytes.Length) bytes) from $url"
        return
      } else {
        Write-Host "SKIP $name - too small ($($bytes.Length)) from $url"
      }
    } catch {
      Write-Host "FAIL $name from $url - $($_.Exception.Message)"
    }
  }
  Write-Host "ALL FAILED for $name"
}

# 1. Garlic Bread - crispy golden, no people
TryDownload 'garlic_bread' @(
  'https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=800&q=90',
  'https://images.unsplash.com/photo-1574781095399-3f17cf4b5d29?w=800&q=90',
  'https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=800&q=90',
  'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=800&q=90'
)

# 2. Latte - ceramic cup with latte art
TryDownload 'latte' @(
  'https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=800&q=90',
  'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=800&q=90',
  'https://images.unsplash.com/photo-1497935586351-b67a49e012bf?w=800&q=90',
  'https://images.unsplash.com/photo-1529892485617-25f63cd7b1e9?w=800&q=90'
)

# 3. Mocha - chocolate mocha with latte art
TryDownload 'mocha' @(
  'https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?w=800&q=90',
  'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800&q=90',
  'https://images.unsplash.com/photo-1551030173-122aabc4489c?w=800&q=90',
  'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800&q=90'
)

# 4. Scone - traditional English scone, NOT steak/meat
TryDownload 'scone' @(
  'https://images.unsplash.com/photo-1563178406-4cdc2923acbc?w=800&q=90',
  'https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=800&q=90',
  'https://images.unsplash.com/photo-1587314168485-3236d6710814?w=800&q=90',
  'https://images.unsplash.com/photo-1621997895729-64e0dd0c5d76?w=800&q=90'
)

# 5. Lemon Cake - lemon sponge with frosting
TryDownload 'lemon_cake' @(
  'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=800&q=90',
  'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=800&q=90',
  'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=800&q=90',
  'https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=800&q=90'
)

Write-Host "`nAll downloads attempted."
