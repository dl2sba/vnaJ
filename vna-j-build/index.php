<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<HTML>
 <HEAD>
<?php

/* PlusIndexes - PHP Script, v1.1
   [place as 'index.php' inside the desired directory]
   copyright 05/17/01 SONDE.ORG - merker@sonde.org */

$directory = preg_replace("http://".getenv("SERVER_NAME"),"",getenv("SCRIPT_URI"));
$path = dirname(getenv("SCRIPT_FILENAME"));
printf('<TITLE>Index of %s</TITLE></HEAD><BODY><H1>Index of %s</H1><PRE>',$directory,$directory);
printf('<table border="0" width="100%%" cellspacing="0" cellpadding="0">');
printf('<tr align="left" height="25" valign="bottom">');
printf('<td width="6%%" height="25" align="left" valign="bottom" nowrap>&nbsp;&nbsp;</td>');
printf('<th width="31%%" align="left" valign="bottom" nowrap><pre><u>Name</u></pre></th>');
printf('<th width="31%%" align="left" valign="bottom" nowrap><pre><u>Last Modified</u></pre></th>');
printf('<th width="31%%" align="left" valign="bottom" nowrap><pre><u>Size</u></pre></th></tr>');
printf('<tr align="left" valign="bottom">');
printf('<td colspan="4"><HR></th></tr>');
function ftype($fname) {
  if (filetype($fname) != "dir") {
    preg_match(".*\.([A-Za-z0-9]{0,4})$",$fname,$rtn);
    switch ($rtn[1]) {
      case "aif"  : { $img = "sound2"; break; }
            case "aiff"  : { $img = "sound2"; break; }
            case "bin" : { $img = "binary"; break; }
            case "cgi" : { $img = "script"; break; }
            case "dvi"  : { $img = "dvi"; break; }
            case "exe" : { $img = "binary"; break; }
            case "gif"  : { $img = "image2"; break; }
            case "gz"  : { $img = "compressed"; break; }
            case "hqx" : { $img = "binhex"; break; }
            case "htm" : { $img = "text"; break; }
            case "html" : { $img = "text"; break; }
            case "jpg"  : { $img = "image2"; break; }
            case "mov"  : { $img = "movie"; break; }
            case "mp3"  : { $img = "sound2"; break; }
            case "mpg"  : { $img = "movie"; break; }
            case "pdf"  : { $img = "pdf"; break; }
            case "php" : { $img = "script"; break; }
            case "php3" : { $img = "script"; break; }
            case "phtml" : { $img = "script"; break; }
            case "png"  : { $img = "image2"; break; }
            case "shtm" : { $img = "text"; break; }
            case "shtml" : { $img = "text"; break; }
            case "sit" : { $img = "compressed"; break; }
            case "tar" : { $img = "tar"; break; }
            case "txt"  : { $img = "compressed"; break; }
            case "wav"  : { $img = "sound2"; break; }
            case "zip" : { $img = "compressed"; break; }
            default    : { $img = "unknown"; break; }
    }
  } else { $img = "dir"; }
  return($img.".gif");
}

function fsize($fname) {
  $set = 0; $ext = array(" Bytes"," kB","MB","GB","TB");
  $objsize = filesize($fname);
  while ($objsize >= pow(1024,$set)) ++$set;
  $objsize = round($objsize/pow(1024,$set-1)*100)/100 . $ext[$set-1];
  return $objsize;
}

printf('<tr height="25" align="left" valign="bottom">');
printf('<td width="6%%" height="25" align="left" valign="bottom" nowrap><img src="/icons/back.gif"></td>');
printf('<td width="31%%" align="left" valign="bottom" nowrap><pre><a href="..">Parent Directory</a></pre></td>');
printf('<td width="31%%" align="left" valign="bottom" nowrap><pre> </pre></td>');
printf('<td width="31%%" align="left" valign="bottom" nowrap><pre> </pre></td></tr>');

for ($dobj=opendir('.');$listing[] = readdir($dobj);); closedir($dobj); asort($listing);

for(reset($listing);list($key,$object) = each($listing);) {
  if ($object != "" && $object != "." && $object != ".." && $object != basename($PHP_SELF)) {
    printf('<tr height="25" align="left" valign="bottom">');
    printf('<td width="6%%" height="25" align="left" valign="bottom" nowrap><img src="/icons/%s"></td>',ftype($object));
    printf('<td width="31%%" align="left" valign="bottom" nowrap><pre> <a href="%s">%s</a></pre></td>',urlencode($object),$object);
    printf('<td width="31%%" align="left" valign="bottom" nowrap><pre>%s</pre></td>',date("d-M-Y H:i", filectime($object)));
    printf('<td width="31%%" align="left" valign="bottom" nowrap><pre>%s</pre></td></tr>',fsize($object));
  }
}
?></table></blockquote></PRE><HR>
<ADDRESS><?php echo getenv("SERVER_SOFTWARE")." at ".getenv("SERVER_NAME"); ?> Port 80</ADDRESS>
</BODY></HTML>
