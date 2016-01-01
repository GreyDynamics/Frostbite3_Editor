from struct import unpack
f=open("initfs_Win32","rb")
magic=unpack(">I",f.read(4))[0]
if magic in (0x00D1CE00,0x00D1CE01): #the file is XOR encrypted and has a signature
    f.seek(296) #skip the signature
    key=[ord(f.read(1))^123 for i in xrange(260)] #bytes 257 258 259 are not used; XOR the key with 123 right away
    encryptedData=f.read()
    f.close()
    data="".join([chr(key[i%257]^ord(encryptedData[i])) for i in xrange(len(encryptedData))]) #go through the data applying one key byte on one data 
f.close()
t=open("initfs_Win32.txt","wb")
t.write(data)
t.close()