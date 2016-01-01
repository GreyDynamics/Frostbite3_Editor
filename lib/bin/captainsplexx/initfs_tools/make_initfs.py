from struct import unpack
initfs=open("initfs_Win32","rb")
magicB=initfs.read(4)
magic=unpack(">I",magicB)[0]
if magic in (0x00D1CE00,0x00D1CE01): #the file is XOR encrypted and has a signature
    signature=initfs.read(292)
    key=[ord(initfs.read(1))^123 for i in xrange(260)] #bytes 257 258 259 are not used; XOR the key with 123 right away
    initfs.seek(-260, 1)
    keyB=initfs.read(260)
initfs.close()
changedinitfs=open("initfs_Win32.txt","rb")
textdata=changedinitfs.read()
changedinitfs.close()
t=open("initfs_Win32_new","wb")
data="".join([chr(key[i%257]^ord(textdata[i])) for i in xrange(len(textdata))]) #go through the data applying one key byte on one data 
t.write(magicB)
t.write(signature)
t.write(keyB)
t.write(data)
t.close()
