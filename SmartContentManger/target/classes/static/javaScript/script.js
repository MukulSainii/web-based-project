console.log("this is script file");
//coding for side bar 
const toggleSidebar=()=>{
      if($('.sidebar').is(":visible")){
        //true-if visible to use band karna h 
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left","0%")
      }else{
        //false- if not visible to use show karna h
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left","20%")
      }
};


const search=()=>{
    let query=$("#search-input").val();
    if (!query) {
      $(".search-result").hide();
      return; // Exit the function if query is undefined or empty
  }else{
      //search
      console.log(query);
     //sending request to server
     let url=`http://localhost:8080/search/${query}`;
        fetch(url)
        .then((response)=>{
          return response.json();
        })
        .then((data)=>{
          //data
          //console.log(data);

          let text=`<div class='list-group'>`;
              data.forEach((contact)=>{
                text+=`<a href='/user/contact/${contact.Cid}' class='list-group-item list-group-item-action'>${contact.name} </a>`
              });
          text+=`</div>`;
          $(".search-result").html(text);
          $(".search-result").show();

        });

      $(".search-result").show();
     }
  };

  //first request to create order
  const paymentStart=()=>{
    console.log("payment Started...");
    let amount=$("#payment_field").val();
    console.log(amount);
    if(amount==''|| amount==null){
     
      swal("failed!", "amount is required!!", "error");
      return;
    }


    //code for sending request on server
    //we will use ajax-jquery to send request on server to create order
     $.ajax(
      {
          url:'/user/create_order',
          data:JSON.stringify({amount:amount,info:'order_request'}),
          contentType:'application/json',
          type:'POST',
          dataType:'json',
          success:function(response){
            //this function will invoke when success
            console.log(response);
            if(response.status=="created"){
              //open payment form
              let options={
                key:'rzp_test_aetInMhOh05zJ3',
                amount:response.amount,
                currency:'INR',
                name:'Smart contact manager',
                description:'Donation',
                image:'https://www.learncodewithdurgesh.com/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Flcwd_logo.45da3818.png&w=1080&q=75',
                order_id:response.id,
                handler:function(response){
                  console.log(response.razorpay_payment_id);
                  console.log(response.razorpay_order_id);
                  console.log(response.razorpay_signature);
                  console.log('payment successfull.');

                  updatePaymentOnServer(
                    response.razorpay_payment_id,
                    response.razorpay_order_id,
                   
                    "paid"
                  );
                  // alert("congrates!! payement successfull");
                },
                prefill: { //We recommend using the prefill parameter to auto-fill customer's contact information especially their phone number
                  name: "", //your customer's name
                  email: "",
                  contact: "", //Provide the customer's phone number for better conversion rates 
                },
                notes: {
                  address: "Learn code with durgesh",
              },
              theme: {
                color: "#3399cc",
            },
              };

             var rzp1 = new Razorpay(options);
                  rzp1.on('payment.failed', function (response){
                  console.log(response.error.code);
                  console.log(response.error.description);
                  console.log(response.error.source);
                  console.log(response.error.step);
                  console.log(response.error.reason);
                  console.log(response.error.metadata.order_id);
                  console.log(response.error.metadata.payment_id);
                  // alert("Oops payment failed");
                  swal("failed!", "Oops payment failed", "error");
                  });
                  rzp1.open();
            }
          },
          error:function(error){
            //invoke when error
            console.log(error)
            alert("somthing went wrong!!");
          },

     }
     );


  };

 function updatePaymentOnServer(payment_id,order_id,status)
  {
    $.ajax({
      url:'/user/update_order',
      data:JSON.stringify({payment_id:payment_id,order_id:order_id,status:status}),
      contentType:'application/json',
      type:'POST',
      dataType:'json',
      success:function(response)
      {   
         swal("Good job!", "congrates!! payement successfull", "success");
       },
      error:function(error){
        swal("failed!", "Your payment is successfull but we didn't get it on server ,we will contact you as soon as possible", "error");
      },
    });
    
  }