import socket
import numpy as np
import cv2
import threading 
import time 






def casa(casa,celulares):
    while True:
        
        
        tamanho = casa[1].recv(2)
        data = casa[1].recv(tamanho)
        
        
        for celular in celulares:
            if(casa[0].equal(celular[0]) ):
                        
                celular[1].send(data+"\n")
    
    

def aceitaClientes(celulares,casas):
    while True:
        
        conexao, cliente = tcp.accept()
        tipo = conexao.recv(1)
        tamanho = conexao.recv(2)
        user = conexao.recv(int(tamanho))
        
        
        if(int(tipo) == 1):
            casa = [user,conexao,cliente]
            casathr = threading.Thread(target=(casa),args=(casa,celulares))
            casathr.start()
            
            casas.append([casa,casathr])
            print 'Casa - Conectado por', cliente
            
            
        if(int(tipo) == 2):
            
            casas.append([user,conexao,cliente])
            print 'celular - Conectado por', cliente
            
            
            
        
             





HOST = '192.168.43.183'              # Endereco IP do Servidor      # Porta que o Servidor esta
PORT = 5000
tcp = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
orig = (HOST, PORT)
tcp.bind(orig)
tcp.listen(1)

print(tcp.getsockname())


celulares = []
casas = []

aceitaClientesthr = threading.Thread(target=(aceitaClientes),args=(celulares,casas))
aceitaClientesthr.start()




            
    
    
    
    
   