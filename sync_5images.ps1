$src = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'
$tgt = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\target\classes\static\images\combos'

$files = @('garlic_bread.jpg', 'latte.jpg', 'mocha.jpg', 'scone.jpg', 'lemon_cake.jpg')
foreach ($f in $files) {
  Copy-Item "$src\$f" "$tgt\$f" -Force
  Write-Host "Copied: $f"
}
Write-Host "Done."
