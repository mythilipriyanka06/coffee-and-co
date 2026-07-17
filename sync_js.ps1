$src = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\js\premium.js'
$tgt = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\target\classes\static\js\premium.js'
$srcSize = (Get-Item $src).Length
$tgtSize = (Get-Item $tgt).Length
Write-Host "src size: $srcSize"
Write-Host "tgt size: $tgtSize"
if ($srcSize -ne $tgtSize) {
  Copy-Item $src $tgt -Force
  Write-Host "Copied premium.js to target"
} else {
  Write-Host "Files match - no copy needed"
}
