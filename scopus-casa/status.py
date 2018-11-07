# -*- coding: utf-8 -*-


import time
import threading 
from settings import Settings

class Status(object):
    
    
    def __init__(self):
        self.comodos = []
        
        self.settings = Settings()
        
        self.stop = False
        
        
        self.carregaStatusthr = threading.Thread(target=(self.carregaStatus),args=())
        self.carregaStatusthr.start()
    
    
    def novoComodo(self,comodo,status,frame):
        item = [comodo,status,frame,-1]
        self.comodos.append(item)
        return self.comodos.index(item)
        
        
        
    def carregaStatus(self):
        
        time.sleep(12)
        
        while(not self.stop):
            time.sleep(0.5)
            
            text = ""
            for comodo in self.comodos:
                if(comodo[1] != comodo[3]):
                    comodo[3] = comodo[1]
                    text += "\n"+comodo[0]+": "
                    if(comodo[1] == self.settings.COMODO_VAZIO ):
                        text += "comodo vazio";
                    elif(comodo[1] == self.settings.OBJETO_NO_COMODO ):
                        text += "objeto no comodo"
                    elif(comodo[1] == self.settings.PESSOA_NO_COMODO ):
                        text += "pessoa no comodo"
                    elif(comodo[1] == self.settings.ALERTA_RISCO ):
                        text += "ALERTA DE RISCO"
                    elif(comodo[1] == self.settings.SAIU_DA_CASA ):
                        text += "saiu da casa"
                    elif(comodo[1] == self.settings.PODE_ESTAR_NO_BANHEIRO ):
                        text += "pode estar no banheiro"
                
            if(len(text) > 0):
                print(text)
    
    
    def setStatusComodo(self,indice, status,frame):
        self.comodos[indice][1] = status
        self.comodos[indice][2] = frame
    
    
    def getFrameComodo(self,indice):
        return self.comodos[indice][2]
    
    
    def getStatusComodo(self,indice):
        return self.comodos[indice][1]
    
    
    
    
    
    
    
    
    
    
    