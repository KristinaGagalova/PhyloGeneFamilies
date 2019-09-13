#!/bin/bash

echo don\'t forget to make a jar, first!

cd ..
tar zcf /tmp/trust.tgz Align
cd -

scp /tmp/trust.tgz www.ibivu.cs.vu.nl:/zeus/ibivu/www/programs/trustwww/
scp /tmp/trust.jar www.ibivu.cs.vu.nl:/zeus/ibivu/apps/trust

rm /tmp/trust.tgz
rm /tmp/trust.jar
echo Done!

