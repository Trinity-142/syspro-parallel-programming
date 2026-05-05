2. Не lock-free, так как возможно следующее исполнение: F1i - F2i - F3i - F4i - F1j - F2j - F3j - F4j - F5i - F8i -  
F9i - F3i - F4i - F5j - F8j - F9j - F3j - F4j - F5i - ...  
тем самым получится livelock
1) Отсутствие wait-free следует из отсутствия lock-free
3. Да, obstruction-free. После остановки потока `j` массив `flags` мог остаться в 4 возможных состояниях:  

| № | flags[i] | flags[j] |  
|---| --- | --- |  
| 1 | false | false |  
| 2 | false | true |  
| 3 | true | false |  
| 4 | true | true | 
в любом случае, где бы ни оказался поток `i` в момент остановки потока `j`, он всегда сможет выполнить F8 - F9 - F4, после чего состояние флагов станет [true, false] и он сможет дойти до крит секции `F7`  
5. Не deadlock-freedom, так как возможно исполнение из пункта 2.
4) Отсутствие starvation-freedom следует из отсутствия deadlock-freedom