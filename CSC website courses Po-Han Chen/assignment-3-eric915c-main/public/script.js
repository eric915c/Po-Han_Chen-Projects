const mycanvas = document.getElementById('mycanvas');//Get canvas element
const ctx = mycanvas.getContext('2d'); //Get 2D rendering context
const congratsDiv = document.getElementById('congrats');//Get congrats message div
const resetBtn = document.getElementById('resetBtn'); //Get reset button

const padding = 10;//Padding between cards
let cardWidth; //Width of each card
let cardHeight; //Height of each card

const pairsToMatch=[//Pairs of cards to match
    [1, 1],
    [2, 2],
    [3, 3],
    [4, 4]
];

const cards =[//Array of cards with IDs and colors
    { id: 1,color: 'red'},
    { id: 1,color: 'red' },
    { id: 2,color: 'blue' },
    { id: 2,color: 'blue' },
    { id: 3,color: 'green' },
    { id: 3,color: 'green' },
    { id: 4,color: 'pink' },
    { id: 4,color: 'pink' }
];

let flippedCards= []; //Array to store flipped cards

let matchedPairs =0;//Count of matched pairs

function calculateCardSize(){//calculate size of each card based on canvas dimensions
    const TheWidth=mycanvas.width-padding*5;
    const TheHeight=mycanvas.height-padding*3;
    cardWidth=TheWidth/ 4;
    cardHeight=TheHeight/2;
}
//shuffle an array
function shuffle(array){ //I borrowed the idea from chatGPT
    return array.sort(() => Math.random()-0.5);
}


//draw a card on the canvas
function drawCard(x,y,id,color,flipped){
    ctx.fillStyle =flipped ?color:'gray';
    ctx.fillRect(x,y,cardWidth,cardHeight);
    if (flipped){
        ctx.fillStyle ='white';
        ctx.font ='20px Arial';
        ctx.fillText(id,x+ 10, y + cardHeight / 2);
    }
}
//draw the initial board state
function drawBoard(){
    ctx.clearRect(0,0,mycanvas.width,mycanvas.height);
    let index = 0;
    for (let i =0; i<2;i++) {
        for (let j=0;j<4; j++) {
            const x=j*(cardWidth+padding)+padding;
            const y=i*(cardHeight+padding)+padding;
            drawCard(x,y,cards[index].id, cards[index].color,false);
            index++;
        }
    }
}
//check if the game has been won
function checkWin(){
    if (matchedPairs===pairsToMatch.length) {
        congratsDiv.style.display='block';
        resetBtn.style.display='block';
    }
}
//handle click events on the canvas
function handleClick(event){ //I borrowed the idea from chatGPT, only changed the delay time 1000 to 100
    const rect=mycanvas.getBoundingClientRect();
    const mouseX=event.clientX-rect.left;
    const mouseY=event.clientY-rect.top;

    const c =Math.floor(mouseX /(cardWidth +padding));
    const r =Math.floor(mouseY /(cardHeight +padding));
    const index =r*4+c;

    if (!flippedCards.includes(index)){//If the clicked card is not already flipped
        flippedCards.push(index);//Add clicked card to flipped cards array
        drawCard(c*(cardWidth+padding)+padding,r *(cardHeight+padding) +padding,cards[index].id,cards[index].color,true);//Draw flipped card
        if (flippedCards.length===2){//If two cards are flipped
            setTimeout(()=>{//Wait a bit before checking for match
                const[card1, card2]=flippedCards;// Get indices of flipped cards
                const id1=cards[card1].id;
                const id2=cards[card2].id;
                if (pairsToMatch.some(pair=>pair[0]===id1&&pair[1]===id2)) {//If the cards form a matching pair
                    matchedPairs++;//Increment matched pairs count
                    checkWin();//Check if game is won
                } else {//If the cards do not match
                    drawCard(card1%4*(cardWidth+padding)+padding,Math.floor(card1/4)*(cardHeight+padding)+padding,cards[card1].id,cards[card1].color,false);
                    drawCard(card2%4*(cardWidth+padding) +padding,Math.floor(card2/4)*(cardHeight+padding)+padding,cards[card2].id,cards[card2].color,false);
                }
                flippedCards =[];//Reset flipped cards array
            }, 100);//Delay for better visual effect
        }
    }
}
//reset the game
function resetGame(){
    congratsDiv.style.display ='none';//Hide congrats message
    resetBtn.style.display ='none';//Hide reset button
    shuffle(cards);//Shuffle cards
    flippedCards=[];//Reset flipped cards array
    matchedPairs=0;//Reset matched pairs count
    drawBoard();//Redraw board
}
//Event listener for click events on the canvas
mycanvas.addEventListener('click',handleClick);
//Event listener for click events on the reset button
resetBtn.addEventListener('click',resetGame);
//Initial setup
calculateCardSize();//Calculate card size based on canvas dimensions
shuffle(cards);//Shuffle cards
drawBoard();//Draw initial board state
