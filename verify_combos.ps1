$wc = New-Object System.Net.WebClient
$base = 'http://localhost:8080/images/combos/'
$images = @(
  'cappuccino.jpg', 'garlic_bread.jpg', 'espresso.jpg', 'brownie.jpg',
  'latte.jpg', 'chocolate_croissant.jpg', 'mocha.jpg', 'cheese_tart.jpg',
  'americano.jpg', 'blueberry_muffin.jpg', 'flat_white.jpg', 'scone.jpg',
  'cold_brew.jpg', 'chocolate_cookie.jpg', 'tea.jpg', 'lemon_cake.jpg'
)
foreach ($img in $images) {
  try {
    $b = $wc.DownloadData($base + $img)
    Write-Host "OK: $img ($($b.Length) bytes)"
  } catch {
    Write-Host "FAIL: $img"
  }
}
