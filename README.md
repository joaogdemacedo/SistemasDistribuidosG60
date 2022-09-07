# SistemasDistribuidos Grupo 60

Este relatório tem como objectivo a apresentação do desenvolvimento deste projecto e das
decisões tomadas durante a sua realização. O projecto consiste numa aplicação distribuı́da
em que permite reservar e usar servidores virtuais para processamento e armazenamento.
A aplicação desenvolvida deve ser capaz de efectuar requisitos tais como, a autenticação e
registo de um Client dado um e-mail e uma password, podendo após a autenticação fazer a
reserva a pedido ou a reserva de uma instância em leilão.

## Estrutura do Relatório

A funcionalidade desta aplicação é constituı́da por várias fases, tendo inicio na ligação
entre o Client e a Cloud. Após esta ligação, o Cliente é lhe permitido várias funcionalidades
que são geridas através de uma gestão de threads feita pela Cloud.
Esta gestão de threads e toda a estrutura necessária para a sua funcionalidade será expli-
cada detalhadamente nas seguintes sub-secções.(ver esta estrutura no final).

### Client - Cloud

Quando o Client inicia a aplicação é estabelicida uma conexão entre o Client e a Cloud (que é o servidor da aplicação) através de sockets TCP. Quando a Cloud recebe essa conexão ela vai lançar uma thread, ServerWorker, que ficará responsável pela conexão com esse Client. Quando a thread é lançada ela permite que o Client efetue o registo, no caso de não se encontrar registado, ou efetue o login no sistema. Após o login ser efetuado, a thread ServerWorker ficará encarregue de receber os pedidos do cliente e tratar desses mesmos pedidos. Esta thread apresenta um estado partilhado, o objeto Cloud, que contém todos os dados do sistema. Para além desta thread outras threads são lançadas ao longo do programa, que são responsáveis por tratar de licitações e compras de servidores, quando estes não estão disponı́veis. Estas duas threads serão apresentadas com maior detalhe posteriormente.

### Reserva de um servidor a pedido

Para fazer a reserva de um servidor a pedido, o Client envia através do TCP socket essa intenção à sua thread ServerWorker, que por sua vez lhe dispõe ao Client os vários tipos de Server. O Client escolhe o tipo de Server que pretende e independentemente do tipo que escolhe, o ServerWorker vai ver se há algum Server desse tipo com a flag estado a ”livre”primeiramente e depois se há com o estado ”leilao”. Caso exista um com o estado a livre, esse Server é adicionado à lista de Server do Client e o estado alterado para ocupado. Se o Server encontrado disponı́vel tem o estado ”leilao”, é retirado o Server ao atual Client, sendo este notificado, e inserido na lista de Server do Client que reservou a pedido, alterando o estado para ”ocupado”. Se após a verificação não existir algum Server disponı́vel, tem de se acumular as propostas feitas pelos diversos Client. Para isso é criada uma thread para que a proposta de reserva
a pedido do cliente fique em standby, sendo esta de volta ativa quando houver um Server disponı́vel pela libertação de um Server por um outro Client. Quando a thread fica ativa, adiciona o Server ao Client que fez a proposta e é lhe notificado que adquiriu um novo servidor na área das notificações. Esta habilidade de criar uma thread para a proposta ficar standby é feita para que a ligação entre o Client e o ServerWorker não fique stanby. Se esta ficasse de tal modo, o Client não conseguiria fazer mais nada enquanto um Server não fosse libertado.

### Reservar uma instância em leilão

No caso da reserva de uma instância de leilão, o procedimento é semelhante ao da reserva a pedido. O Client ao indicar o tipo Server que pretende licitar, vai indicar o valor que quer licitar e este valor inserido, vai ser testado pelo ServerWorker por este ter que ser menor que o valor do Server quando é reservado a pedido. Após esta verificação o ServerWorker vai verificar se há Server desse tipo com o estado a ”livre”, em que caso tenha, o Client adquire o
Server e a flag estado é atualizada para ”leilao”. Caso contrário, procede-se como na reserva a pedido. É criada uma thread para ficar em standby de modo a que se possa acumular licitações e o Client possa usufruir da aplicação apesar de ter a licitação em stanby. Se um Server for libertado, e não haja nenhum Client em standby para a reserva a pedido desse tipo de Server, vai-se verificar qual das licitações acumuladas para esse tipo é a maior. A thread da licitação maior é ativa e o Client que a obtém, adquire o Server, alterando o estado para ”leilao”e a sua licitação é eliminada, notificando o Client que adquiriu o Server.